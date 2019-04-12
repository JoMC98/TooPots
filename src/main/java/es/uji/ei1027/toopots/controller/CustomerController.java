package es.uji.ei1027.toopots.controller;


import es.uji.ei1027.toopots.daos.CustomerDao;
import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Customer;
import es.uji.ei1027.toopots.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private CustomerDao customerDao;
    private UsersDao userDao;

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao=customerDao;
    }

    @Autowired
    public void setUserDao(UsersDao userDao) {
        this.userDao = userDao;
    }

    //Llistar tots els clients
    @RequestMapping("/list")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerDao.getCustomers());
        return "customer/list";
    }

    //Afegir un nou client
    @RequestMapping("/add")
    public String addCustomer(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("customer", new Customer());
        return "customer/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(HttpSession session, @ModelAttribute("user") Users user, @ModelAttribute("customer") Customer customer,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "customer/add";
        user.setRol("Customer");
        userDao.addUser(user);
        Users newUser = userDao.getUser(user.getUsername());

        session.setAttribute("user", newUser);

        customerDao.addCustomer(customer, newUser.getId());
        return "redirect:../";
    }

    //Actualitzar un client
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editCustomer(Model model, @PathVariable int id) {
        model.addAttribute("customer", customerDao.getCustomer(id));
        return "customer/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id, @ModelAttribute("customer") Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "customer/update";
        customerDao.updateCustomer(customer);
        return "redirect:../list";
    }

    //Esborra un client
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        customerDao.deleteCustomer(id);
        return "redirect:../list";
    }
}
