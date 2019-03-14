package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.CertificationDao;
import es.uji.ei1027.toopots.model.Certification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/certification")
public class CertificationController {
    private CertificationDao certificationDao;

    @Autowired
    public void setCertificationDao(CertificationDao certificationDao) {
        this.certificationDao=certificationDao;
    }

    //Llistar totes les certificacions
    @RequestMapping("/list")
    public String listCertifications(Model model) {
        model.addAttribute("certifications", certificationDao.getCertifications());
        return "certification/list";
    }

    //Afegir una nova certificacio
    @RequestMapping("/add")
    public String addCertification(Model model) {
        model.addAttribute("certification", new Certification());
        return "certification/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("certification") Certification certification, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "certification/add";
        certificationDao.addCertification(certification);
        return "redirect:list";
    }

    //Actualitzar una certificacio
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editCertification(Model model, @PathVariable int id) {
        model.addAttribute("certification", certificationDao.getCertification(id));
        return "certification/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id, @ModelAttribute("certification") Certification certification, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "certification/update";
        certificationDao.updateCertification(certification);
        return "redirect:../list";
    }

    //Esborra una certificacio
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        certificationDao.deleteCertification(id);
        return "redirect:../list";
    }
}
