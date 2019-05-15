package es.uji.ei1027.toopots.controller;


import es.uji.ei1027.toopots.daos.ActivityTypeDao;
import es.uji.ei1027.toopots.daos.CustomerDao;
import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Customer;
import es.uji.ei1027.toopots.model.Users;
import es.uji.ei1027.toopots.validator.UserValidator;
import es.uji.ei1027.toopots.validator.CustomerValidator;
import org.jasypt.util.password.BasicPasswordEncryptor;
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
    private ActivityTypeDao activityDao;
    private BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

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
        model.addAttribute("users", userDao.getCustomers());
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
    public String processAddSubmit(HttpSession session, @ModelAttribute("user") Users user, BindingResult bindingResultUser, @ModelAttribute("customer") Customer customer,
                                   BindingResult bindingResultCustomer) {

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.validate(customer,bindingResultCustomer);

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user,bindingResultUser);

        if (bindingResultCustomer.hasErrors() || bindingResultUser.hasErrors()) {
            System.out.println(bindingResultCustomer.toString());
            System.out.println(bindingResultUser.toString());

            return "customer/add";
        }
        user.setRol("Customer");
        user.setPasswd(passwordEncryptor.encryptPassword(user.getPasswd()));

        userDao.addUser(user);
        Users newUser = userDao.getUser(user.getUsername());

        session.setAttribute("user", newUser);

        customerDao.addCustomer(customer, newUser.getId());
        return "redirect:../";
    }

    //Actualitzar un client
    @RequestMapping(value="/update", method = RequestMethod.GET)
    public String editCustomer(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        model.addAttribute("user", userDao.getUser(user.getId()));
        model.addAttribute("customer", customerDao.getCustomer(user.getId()));
        return "customer/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("user") Users user, BindingResult bindingResultUser, @ModelAttribute("customer") Customer customer,
                                      BindingResult bindingResultCustomer) {
        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.validate(customer,bindingResultCustomer);

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user,bindingResultUser);

        if (bindingResultCustomer.hasErrors() || bindingResultUser.hasErrors()) {
            System.out.println(bindingResultCustomer.toString());
            System.out.println(bindingResultUser.toString());
            return "customer/update";
        }

        userDao.updateUser(user);
        customerDao.updateCustomer(customer);
        return "redirect:/";
    }

    //Ver una reserva
    @RequestMapping(value="/viewReservation/{id}")
    public String z(@PathVariable int id) {
        customerDao.getReservation();
        return "customer/viewReservation";
    }

    //Esborra un client
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        userDao.deleteUser(id);
        customerDao.deleteCustomer(id);
        return "redirect:../list";
    }
}
