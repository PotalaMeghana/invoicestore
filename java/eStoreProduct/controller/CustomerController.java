package eStoreProduct.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import eStoreProduct.model.Product;
import eStoreProduct.model.custCredModel;
import eStoreProduct.model.productqty;
import eStoreProduct.utility.ProductStockPrice;
import eStoreProduct.BLL.BLL;
import eStoreProduct.BLL.BLLClass2;
import eStoreProduct.model.orderModel;
import eStoreProduct.DAO.OrderDAO;
//import eStoreProduct.BLL.BLLClass;
import eStoreProduct.DAO.ProductDAO;
import eStoreProduct.DAO.cartDAO;
import eStoreProduct.DAO.customerDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class CustomerController {
	customerDAO cdao;
	BLL BLL;
    BLLClass2 bl2;
	//BLLClass obj;
    ProductDAO pdaoimp;
	cartDAO cartimp;
	OrderDAO orderdao;
	@Autowired
	public CustomerController(cartDAO cartdao,customerDAO customerdao,BLLClass2 bl2,BLL bl1 ,ProductDAO productdao,OrderDAO odao) {
		cdao = customerdao;
		cartimp=cartdao;
		this.bl2=bl2;
		this.BLL=bl1;
		pdaoimp=productdao;
		orderdao=odao;
		//cartdao1 = cartdao;
	}
	@RequestMapping(value = "/profilePage")
	public String sendProfilePage(Model model, HttpSession session) {
		custCredModel cust = (custCredModel) session.getAttribute("customer");
		//System.out.println(cust.getCustId());
		model.addAttribute("cust", cust);
		return "profile";
	}

	// on clicking update Profile in profile page
	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public String userupdate(@ModelAttribute("Customer") custCredModel cust, Model model, HttpSession session) {
		cdao.updatecustomer(cust);
		custCredModel custt = cdao.getCustomerById(cust.getCustId());
		//System.out.print(custt.getCustLocation());
		if (custt != null) {
			model.addAttribute("cust", custt);
		}
		return "profile";
	}
	
	@RequestMapping(value = "/invoice", method = RequestMethod.POST)
	public String showPaymentOption(Model model, HttpSession session,HttpServletRequest request) {
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		List<ProductStockPrice> products = cartimp.getCartProds(cust1.getCustId());
		//System.out.println("billno         "+ request.getParameter("razorpay_order_id"));
		//System.out.println("paymentid         "+request.getParameter("razorpay_payment_id"));
		orderModel order=orderdao.getOrderDetails(1,1);
		System.out.println("order obj   "+order);
		model.addAttribute("customer", cust1);
		//model.addAttribute("payid",id);
		model.addAttribute("order", order);
		//model.addAttribute("total",priceString);
		
		return "invoice";
	}
	
	@RequestMapping(value = "/showInvoice", method = RequestMethod.POST)
	public String showInvoice(Model model, HttpSession session) {
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		
		return "invoice";
	}
	
	@PostMapping("/updateshipment")
	@ResponseBody
	public String handleFormSubmission(custCredModel cust,Model model, HttpSession session) {
		//System.out.println(cust.getCustSpincode());
		int pincode = Integer.parseInt(cust.getCustSpincode());
		boolean isValid = pdaoimp.isPincodeValid(pincode);
		if (isValid) {
			session.setAttribute("custspincode",pincode);
			return "Saved";
		} else {
			return "UnSaved";
		}
	}
	@GetMapping("/paymentoptions")
	public String orderCreate(Model model,HttpSession session) {
		//String orderId = bl2.createRazorpayOrder(Double.parseDouble((String) session.getAttribute("qtycost")));
		double var = (double) session.getAttribute("cartcost");
		System.out.println("amount in controller before razor pay  "+var);
		String orderId = bl2.createRazorpayOrder(var);
		System.out.println("oid                  "+orderId);
		//System.out.println("amount in controller "+var);
		model.addAttribute("orderId",orderId);
		//System.out.println("hiiiiii---" +var);
		model.addAttribute("amt", var);
		//model.addAttribute("amt", var);
		return "payment-options";
	}
}
