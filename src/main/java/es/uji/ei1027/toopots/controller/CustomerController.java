package es.uji.ei1027.toopots.controller;


import es.uji.ei1027.toopots.daos.CustomerDao;
import es.uji.ei1027.toopots.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private CustomerDao customerDao;

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao=customerDao;
    }

    //Llistar tots els clients
    @RequestMapping("/list")
    public String listActivities(Model model) {
        model.addAttribute("customers", customerDao.getCustomers());
        return "customer/list";
    }

    //Afegir un nou client
    @RequestMapping("/add")
    public String addCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("customer") Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "customer/add";
        customerDao.addCustomer(customer);
        return "redirect:list";
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
