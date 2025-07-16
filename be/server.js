require('dotenv').config();
const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
app.use(express.static('public')); 

app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization');
    next();
});

const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);

// Tạo Stripe Checkout Session
app.post('/create-checkout-session', async (req, res) => {
    try {
        const { productName, amount } = req.body;
        
        const session = await stripe.checkout.sessions.create({
            payment_method_types: ['card'],
            line_items: [{
                price_data: {
                    currency: 'usd',
                    product_data: {
                        name: productName,
                    },
                    unit_amount: amount,
                },
                quantity: 1,
            }],
            mode: 'payment',
            success_url: `${req.protocol}://${req.get('host')}/success.html?session_id={CHECKOUT_SESSION_ID}`,
            cancel_url: `${req.protocol}://${req.get('host')}/cancel.html`,
            metadata: {
                order_id: Date.now().toString(),
                source: 'android_app'
            }
        });

        res.json({ url: session.url });
    } catch (error) {
        console.error('Stripe error:', error);
        res.status(500).json({ error: error.message });
    }
});

// API để lấy thông tin session
app.get('/api/get-session-details', async (req, res) => {
    const { session_id } = req.query;
    
    try {
        const session = await stripe.checkout.sessions.retrieve(session_id);
        
        res.json({
            success: true,
            order_id: session.metadata.order_id || session.id,
            amount_formatted: formatCurrency(session.amount_total),
            payment_status: session.payment_status,
            customer_email: session.customer_email
        });
    } catch (error) {
        res.json({ success: false, error: error.message });
    }
});

// Format currency helper
function formatCurrency(amount) {
    // Stripe amount is in cents, so we need to divide by 100 for dollars
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount / 100);
}

// Route test
app.get('/', (req, res) => {
    res.send(`
        <h1>FastFood Payment Server</h1>
        <p>Server đang chạy thành công!</p>
        <p><a href="/success.html">Test Success Page</a></p>
        <p><a href="/cancel.html">Test Cancel Page</a></p>
    `);
});

// Webhook để xử lý events từ Stripe 
app.post('/webhook', express.raw({type: 'application/json'}), (req, res) => {
    console.log('Webhook received');
    res.json({received: true});
});

app.listen(PORT, () => {
    console.log(`🚀 Server running on port ${PORT}`);
    console.log(`🌍 Local: http://localhost:${PORT}`);
    console.log(`✅ Success URL: http://localhost:${PORT}/success.html`);
    console.log(`❌ Cancel URL: http://localhost:${PORT}/cancel.html`);
});