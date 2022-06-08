package woowacourse.shoppingcart.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import woowacourse.shoppingcart.domain.CartItem;
import woowacourse.shoppingcart.domain.Customer.Customer;
import woowacourse.shoppingcart.domain.Product;
import woowacourse.shoppingcart.exception.NotFoundProductException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static woowacourse.fixture.Fixture.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private CartItemDao cartItemDao;
    private CustomerDao customerDao;
    private ProductDao productDao;

    private Long customerId;
    private Long productId;

    @BeforeEach
    void setUp() {
        cartItemDao = new CartItemDao(jdbcTemplate);
        customerDao = new CustomerDao(jdbcTemplate);
        productDao = new ProductDao(jdbcTemplate);
        customerId = customerDao.save(Customer.createWithoutId(TEST_EMAIL, TEST_PASSWORD, TEST_USERNAME));
        productId = productDao.save(new Product(PRODUCT_NAME, PRICE, THUMBNAIL_URL, QUANTITY));
    }

    @Test
    @DisplayName("주문 수량을 수정한다.")
    void updateCartItem() {

        int expected = 2;
        cartItemDao.addCartItem(customerId, productId, 1);

        cartItemDao.updateCartItem(customerId, productId, expected);

        final CartItem cartItem = cartItemDao.findCartItemByCustomerIdAndProductId(customerId, productId).get();
        assertThat(cartItem.getCount()).isEqualTo(expected);
    }

    @Test
    @DisplayName("장바구니에 담긴 물건을 삭제한다.")
    void deleteCartItem() {
        cartItemDao.addCartItem(customerId, productId, 1);
        cartItemDao.deleteCartItem(customerId, productId);

    }

    @Test
    @DisplayName("장바구니에 담긴 물건을 삭제할때 존재하지 않는 상품이면 예외가 발생한다.")
    void deleteCartItem_NotFoundException() {
        cartItemDao.addCartItem(customerId, productId, 1);

        Assertions.assertThatThrownBy(() -> cartItemDao.deleteCartItem(customerId, 2L))
                        .isInstanceOf(NotFoundProductException.class)
                        .hasMessage("존재하지 않는 상품 ID입니다.");
    }

    @Test
    @DisplayName("장바구니에 물건을 담는다.")
    void addCartItem() {
        cartItemDao.addCartItem(customerId, productId, 1);

        CartItem cartItem = cartItemDao.findCartItemByCustomerIdAndProductId(customerId, productId).get();
        assertThat(cartItem.getName()).isEqualTo(PRODUCT_NAME);
    }
}
