package com.example.data.models

data class PaintService(
    val id: String,
    val title: String,
    val subtitle: String,
    val pricePerSqFt: Double,
    val priceUnit: String = "₹/sq ft",
    val description: String,
    val features: List<String>,
    val category: String, // Interior, Exterior, Specialty
    val iconName: String = "paint_roller"
)

data class PaintCostEstimate(
    val areaSqFt: Double,
    val roomsCount: Int,
    val serviceTitle: String,
    val ratePerSqFt: Double,
    val paintMaterialCost: Double,
    val laborCost: Double,
    val totalCost: Double,
    val estimatedLiters: Double,
    val estimatedDays: Int
)

data class UserProfile(
    val name: String = "Mansuri Client",
    val phone: String = "+91 98765 43210",
    val email: String = "mansuriusman498@gmail.com",
    val address: String = "Flat 402, Golden Towers, Station Road",
    val isLoggedIn: Boolean = true,
    val isGoogleUser: Boolean = false,
    val isAdmin: Boolean = false,
    val role: String = if (isAdmin) "admin" else "customer"
)

object DefaultServices {
    val list = listOf(
        PaintService(
            id = "s1",
            title = "Royal Paint",
            subtitle = "Ultra Luxury High Sheen Smooth Finish",
            pricePerSqFt = 27.0,
            description = "High-shine luxury emulsion paint for interior walls with Teflon surface protector, stain resistance, and washable coat.",
            features = listOf("Teflon Stain Guard", "Smooth High Gloss Finish", "5 Year Warranty", "Eco-Friendly Low VOC"),
            category = "Interior"
        ),
        PaintService(
            id = "s2",
            title = "Plastic Paint",
            subtitle = "Rich Velvet Matte Finish for Interiors",
            pricePerSqFt = 20.0,
            description = "Smooth washable plastic emulsion paint offering vibrant color depth and high durability for modern homes.",
            features = listOf("Velvet Soft Touch", "Easy Washable", "Anti-Fungal Formula", "Odourless Coating"),
            category = "Interior"
        ),
        PaintService(
            id = "s3",
            title = "Distemper",
            subtitle = "Budget-Friendly Fresh Wall Coating",
            pricePerSqFt = 15.0,
            description = "Economical water-based acrylic distemper that gives bright, clean, smooth matte coverage for all rooms.",
            features = listOf("Cost Effective", "Bright Clean Color", "Quick Drying", "Smooth Coverage"),
            category = "Interior"
        ),
        PaintService(
            id = "s4",
            title = "Putty & Wall Primer",
            subtitle = "Flawless Base Preparation & Leveling",
            pricePerSqFt = 12.0,
            description = "Premium white acrylic wall putty application with double coat sanding and primer application for mirror-smooth walls.",
            features = listOf("Crack Filling", "Double Coat Sanding", "Moisture Resistance", "Maximizes Paint Life"),
            category = "Specialty"
        ),
        PaintService(
            id = "s5",
            title = "Waterproofing",
            subtitle = "Damp-Proof Sealing & Anti-Seepage",
            pricePerSqFt = 35.0,
            description = "Advanced elastomeric chemical waterproofing membrane for damp walls, roofs, terrace, and bathroom leakage prevention.",
            features = listOf("10 Year Leak Seal Guarantee", "Thermal Insulation", "Fungus & Mold Shield", "Flexible Rubber Membrane"),
            category = "Specialty"
        ),
        PaintService(
            id = "s6",
            title = "Texture Paint",
            subtitle = "Designer Accent Wall Patterns & Metallic Art",
            pricePerSqFt = 45.0,
            description = "Custom metallic, marble, stucco, and rustic designer texture coatings created by expert artistic painters.",
            features = listOf("Artistic Stucco Patterns", "Metallic Gold Accents", "3D Wall Texture", "Custom Stencil Art"),
            category = "Specialty"
        ),
        PaintService(
            id = "s7",
            title = "Exterior Painting",
            subtitle = "Weather-Proof All-Season Barrier",
            pricePerSqFt = 25.0,
            description = "Heavy duty weather-proof exterior wall coating with anti-dust technology and anti-fading color stay.",
            features = listOf("Dust Proof Shield", "Anti-Algae Formula", "UV Ray Protection", "Rain & Heat Guard"),
            category = "Exterior"
        ),
        PaintService(
            id = "s8",
            title = "Interior Painting Complete",
            subtitle = "End-to-End Home Makeover Package",
            pricePerSqFt = 22.0,
            description = "Complete interior transformation including furniture covering, wall repair, putty, primer, and double coat finish.",
            features = listOf("Furniture Masking Included", "Deep Cleaning Post Painting", "Laser Measurement", "Color Consultation"),
            category = "Interior"
        )
    )
}
