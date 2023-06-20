package eStoreProduct.DAO;

import eStoreProduct.model.orderModel;

public interface OrderDAO {
	
		//public orderModel getOrderDetails(int oid);

		public orderModel getOrderDetails(int orderid, int custid);

	}

