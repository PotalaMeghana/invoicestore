package eStoreProduct.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eStoreProduct.model.orderModel;
@Component
public class OrderDAOImp implements OrderDAO {

	JdbcTemplate jdbcTemplate;
	
	@Autowired
	public OrderDAOImp(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// this.prodStockDAO = prodStockDAO;
	}
	
	private final String get_order_details="select ordr_id,ordr_cust_id,ordr_billno,ordr_odate,ordr_total,ordr_gst,ordr_payreference,\r\n"
			+ "       ordr_saddress,cust_name,cust_mobile,cust_email,cust_location from slam_orderdup,slam_customers where ordr_cust_id=cust_id and cust_id=? and ordr_id=?";
	
	@Override
	public orderModel getOrderDetails(int orderid,int custid) {
		orderModel order=jdbcTemplate.queryForObject(get_order_details, new Object[] { custid,orderid }, new OrderMapper());
		System.out.println("order obj inn jdbc     "+order);
		return order;
	}

}
