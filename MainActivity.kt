package com.digitalworld.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Typeface
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType

class MainActivity : Activity() {
    private val bg = Color.rgb(13, 18, 37)
    private val panel = Color.rgb(25, 36, 64)
    private val accent = Color.rgb(54, 185, 245)
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private val products = mutableListOf("Headphones", "Smart Watch", "Mobile Accessories")
    private val services = mutableListOf("Digital Assistance", "Education Services", "Digital Marketing", "Business Promotion")
    private val orders = mutableListOf<String>()
    private val prefs by lazy { getSharedPreferences("dw", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun setup(title: String) {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }
        val header = TextView(this).apply {
            text = "🌐  DIGITAL WORLD"
            textSize = 21f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(18, 20, 12, 18)
            setBackgroundColor(Color.rgb(21, 29, 56))
        }
        root.addView(header)
        val bar = TextView(this).apply {
            text = title
            textSize = 19f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(18, 15, 18, 12)
        }
        root.addView(bar)
        val scroll = ScrollView(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 6, 16, 20)
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.rgb(21, 29, 56))
        }
        listOf("Home", "Services", "Products", "Orders", "Admin").forEach { label ->
            val b = Button(this).apply {
                text = label
                textSize = 10f
                isAllCaps = false
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    when(label) {
                        "Home" -> showHome()
                        "Services" -> showServices()
                        "Products" -> showProducts()
                        "Orders" -> showOrders()
                        "Admin" -> showAdmin()
                    }
                }
            }
            nav.addView(b, LinearLayout.LayoutParams(0, 54, 1f))
        }
        root.addView(nav)
        setContentView(root)
    }

    private fun text(s: String, size: Float = 15f, color: Int = Color.WHITE, bold: Boolean = false) {
        val v = TextView(this).apply {
            text = s
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
            setPadding(2, 7, 2, 7)
        }
        content.addView(v)
    }

    private fun button(label: String, action: () -> Unit) {
        val b = Button(this).apply {
            text = label
            isAllCaps = false
            setTextColor(Color.rgb(5, 20, 38))
            setBackgroundColor(accent)
            setOnClickListener { action() }
        }
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(0, 6, 0, 8)
        content.addView(b, lp)
    }

    private fun card(title: String, desc: String, actionLabel: String? = null, action: (() -> Unit)? = null) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15, 12, 15, 12)
            setBackgroundColor(panel)
        }
        val t = TextView(this).apply {
            text = title
            textSize = 17f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        }
        val d = TextView(this).apply {
            text = desc
            textSize = 13f
            setTextColor(Color.LTGRAY)
            setPadding(0, 6, 0, 6)
        }
        box.addView(t); box.addView(d)
        if (actionLabel != null && action != null) {
            val b = Button(this).apply {
                text = actionLabel
                isAllCaps = false
                setOnClickListener { action() }
            }
            box.addView(b)
        }
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(0, 6, 0, 10)
        content.addView(box, lp)
    }

    private fun showHome() {
        setup("Business Dashboard")
        text("Welcome to Digital World", 25f, accent, true)
        text("Your smart business companion. Manage services, products and customer requests from one place.")
        card("🧰  Business Services", "Explore digital assistance, education and promotion services.") { }
        card("🛍️  Products", "Browse your product catalogue.", "View Products") { showProducts() }
        card("📦  Customer Orders", "${orders.size} order request(s) saved on this device.", "Manage Orders") { showOrders() }
        card("📲  WhatsApp Support", "Connect customers with your business.", "Contact on WhatsApp") { contactWhatsApp() }
        text("Note: This starter app stores order requests locally on this phone.")
    }

    private fun showServices() {
        setup("Our Services")
        services.forEach { s -> card("✨  $s", "Contact us to ask about availability and details.", "Request Service") { placeOrder(s) } }
    }

    private fun showProducts() {
        setup("Products")
        val search = EditText(this).apply {
            hint = "Search products"
            setSingleLine()
        }
        content.addView(search)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(list)
        fun refresh(q: String = "") {
            list.removeAllViews()
            products.filter { it.contains(q, true) }.forEach { p ->
                val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(14,10,14,10); setBackgroundColor(panel) }
                box.addView(TextView(this).apply { text = "🛒  $p"; textSize=17f; setTextColor(Color.WHITE) })
                box.addView(TextView(this).apply { text = "Price: Contact for price"; setTextColor(Color.LTGRAY) })
                box.addView(Button(this).apply { text="Place Order"; setOnClickListener { placeOrder(p) } })
                val lp = LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,6,0,8); list.addView(box,lp)
            }
        }
        search.addTextChangedListener(object: android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { refresh(s.toString()) }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        refresh()
    }

    private fun placeOrder(item: String) {
        setup("New Request")
        text("Request: $item", 19f, accent, true)
        val name = EditText(this).apply { hint = "Your name" }
        val phone = EditText(this).apply { hint = "Phone number"; inputType = InputType.TYPE_CLASS_PHONE }
        val details = EditText(this).apply { hint = "Address / requirements" }
        content.addView(name); content.addView(phone); content.addView(details)
        button("Save Request") {
            if (name.text.isBlank() || phone.text.isBlank()) {
                Toast.makeText(this, "Please enter name and phone number", Toast.LENGTH_SHORT).show()
            } else {
                orders.add("$item | ${name.text} | ${phone.text} | ${details.text}")
                Toast.makeText(this, "Request saved on this device", Toast.LENGTH_LONG).show()
                showOrders()
            }
        }
        button("Send on WhatsApp") {
            val msg = "Business request\nItem: $item\nName: ${name.text}\nPhone: ${phone.text}\nDetails: ${details.text}"
            shareWhatsApp(msg)
        }
    }

    private fun showOrders() {
        setup("Order Management")
        text("Saved requests: ${orders.size}", 18f, accent, true)
        if (orders.isEmpty()) text("No requests yet. New customer requests will appear here.")
        orders.forEachIndexed { i, order ->
            card("Request #${i+1}", order, "Delete") {
                orders.removeAt(i); showOrders()
            }
        }
    }

    private fun showAdmin() {
        setup("Admin Panel")
        text("Local Admin Tools", 20f, accent, true)
        text("This starter admin screen is not password-protected. Do not store sensitive customer data here.")
        val product = EditText(this).apply { hint = "New product name" }
        content.addView(product)
        button("Add Product") {
            if (product.text.isNotBlank()) {
                products.add(product.text.toString())
                Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show()
                product.setText("")
            }
        }
        val service = EditText(this).apply { hint = "New service name" }
        content.addView(service)
        button("Add Service") {
            if (service.text.isNotBlank()) {
                services.add(service.text.toString())
                Toast.makeText(this, "Service added", Toast.LENGTH_SHORT).show()
                service.setText("")
            }
        }
        button("View Orders") { showOrders() }
    }

    private fun contactWhatsApp() {
        val number = prefs.getString("whatsapp", "") ?: ""
        if (number.isBlank()) {
            Toast.makeText(this, "Set WhatsApp number in Admin settings in a future update.", Toast.LENGTH_LONG).show()
            return
        }
        shareWhatsApp("Hello, I need information about your services.")
    }

    private fun shareWhatsApp(message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/?text=" + Uri.encode(message)))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp or a browser is not available.", Toast.LENGTH_SHORT).show()
        }
    }
}
