import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.CartDisplayItem

data class Order(
    var id: String = "",
    val userId: String = "",
    val address: Address,
    val cartItems: List<CartDisplayItem>,
    val paymentMethod: String,
    val subtotal: Double,
    val shipping: Double,
    val total: Double,
    val timestamp: Long = System.currentTimeMillis()
)