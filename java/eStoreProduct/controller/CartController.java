package eStoreProduct.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import eStoreProduct.model.custCredModel;
import eStoreProduct.model.productqty;
import eStoreProduct.utility.ProductStockPrice;
import eStoreProduct.BLL.BLL;
import eStoreProduct.BLL.BLLClass2;
import eStoreProduct.DAO.ProductDAO;
import eStoreProduct.DAO.cartDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import eStoreProduct.model.cartModel;

import javax.servlet.http.HttpServletRequest;
//import eStoreProduct.BLL.BLLClass;
import javax.servlet.http.HttpSession;

@Controller
public class CartController {
	cartDAO cartimp;
	private final ProductDAO pdaoimp;
	List<ProductStockPrice> alist = new ArrayList<>();

	 BLL BLL;// = new BLLClass();
	@Autowired
	public CartController(cartDAO cartdao, ProductDAO productdao,BLL b) {
		cartimp = cartdao;
		BLL=b;
		pdaoimp = productdao;
	}

	@GetMapping("/addToCart")
	@ResponseBody
	public String addToCart(@RequestParam(value = "productId", required = true) int productId, Model model,
			HttpSession session) throws NumberFormatException, SQLException {
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		if (cust1 != null) {
			return cartimp.addToCart(productId, cust1.getCustId()) + " Added to cart";
		} else {
			ProductStockPrice product = pdaoimp.getProductById(productId);
			alist.add(product);
			model.addAttribute("products", alist);
			return "added to cart";

		}
	}

	@RequestMapping(value = "/cartItems", method = RequestMethod.GET)
	public String getSignUpPage(Model model, HttpSession session,HttpServletRequest request) {
		double cartt = 0;
		// ProductDAO pdao = new ProductDAO();
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		if (cust1 != null) {
			List<ProductStockPrice> products = cartimp.getCartProds(cust1.getCustId());
			model.addAttribute("products", products);
			double cartcost=BLL.getCartCost(cust1.getCustId());
			model.addAttribute("cartcost",cartcost);
			model.addAttribute("cust",cust1);
			return "cart";
		} else {
			double cartcost=cartimp.getCartCostNonLogin((List<ProductStockPrice>)request.getAttribute("products"));
			model.addAttribute("cartcost",cartcost);
			model.addAttribute("products", alist);
			return "cart";

		}
	}

	@GetMapping("/removeFromCart")
	@ResponseBody
	public String removeFromCart(@RequestParam(value = "productId", required = true) int productId, Model model,
			HttpSession session) throws NumberFormatException, SQLException {
		 System.out.println("pid remove from cart "+productId);

		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		if (cust1 != null) {
			System.out.println("remove from cart login");
			return cartimp.removeFromCart(productId, 1) + " remove from cart";
		} else {
			// Product product=pdaoimp.getProductById(productId);
			// System.out.println("remove from cart nonlogin");
			for (ProductStockPrice p : alist) {
				if (p.getProd_id() == productId)

					alist.remove(p);
			}

			return "remove from cart";
		}

	}

	@PostMapping("/updateQuantity")
	@ResponseBody
	public String updateQuantity(@RequestParam(value = "productId", required = true) int productId,
			@RequestParam(value = "quantity", required = true) int quantity, Model model, HttpSession session)
			throws NumberFormatException, SQLException {
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		if (cust1 != null) {
			//System.out.println("custid  "+cust1.getCustId());
			cartModel cart = new cartModel(cust1.getCustId(), productId, quantity);
		    cartimp.updateQty(cart);
		    List<ProductStockPrice> products = cartimp.getCartProds(cust1.getCustId());
			//model.addAttribute("products", products);
			String cartcost=String.valueOf(BLL.getCartCost(cust1.getCustId()));
			//model.addAttribute("cartcost",cartcost);
			System.out.println("done updating quantity");
		  return cartcost;
		} else {
			for (ProductStockPrice product : alist) {
				if (product.getProd_id() == productId) {
					product.setQuantity(quantity);
				}
			}
			
		}
		return "hi";
	}
	@GetMapping("/buycartitems")
	public String confirmbuycart(Model model, HttpSession session) {

		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		if (cust1 != null) {
			List<ProductStockPrice> products = cartimp.getCartProds(cust1.getCustId());
			model.addAttribute("products", products);
			model.addAttribute("cust1",cust1);
			double cartcost=BLL.getCartCost(cust1.getCustId());
			model.addAttribute("cartcost",cartcost);
			 session.setAttribute("cartcost", cartcost); 
			//model.addAttribute("products", products);

			return "paymentcart";
		} else {
			return "signIn";
		}

	}
	
}