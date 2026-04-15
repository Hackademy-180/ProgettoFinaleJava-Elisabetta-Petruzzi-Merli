package it.aulab.progetto_finale_java.controllers;

import java.security.Principal;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.models.CareerRequest;
import it.aulab.progetto_finale_java.models.Role;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.RoleRepository;
import it.aulab.progetto_finale_java.repositories.UserRepository;
import it.aulab.progetto_finale_java.services.CareerRequestService;

@Controller
@RequestMapping("/operations")
public class OperationController {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRequestService careerRequestService;

    // rotta per la creazione di una  richiesta di collaborazione
    @GetMapping("/career/request")
    public String careerRequestCreate(Model viewModel){
        viewModel.addAttribute("title", "Inserisci la tua richiesta");
        viewModel.addAttribute("careerRequest", new CareerRequest());
        
        List<Role> roles = roleRepository.findAll();
        // elimino la possibilità di scegliere il ruolo user nella select del form
        roles.removeIf(e -> e.getName().equals("ROLE_USER"));
        viewModel.addAttribute("roles", roles);

        return "career/requestForm";
    }

    // rotta per il salvataggio di una richiesta di ruolo
    @PostMapping("/career/request/save")
    public String careerRequestStore(@ModelAttribute("careerRequest") CareerRequest careerRequest, Principal principal, RedirectAttributes redirectAttributes){
        User user = userRepository.findByEmail(principal.getName());

        if(careerRequestService.isRoleAlreadyAssigned(user,careerRequest)){
            redirectAttributes.addFlashAttribute("erroMessage", "Sei già assegnato a questo ruolo");
            return "redirect:/";
        }

        careerRequestService.save(careerRequest, user);

        redirectAttributes.addFlashAttribute("successMessage", "Richiesta inviata con successo");
        return "redirect:/";
    }

    // rotta per il dettaglio di una richiesta
    @GetMapping("/career/request/detail/{id}")
    public String careerRequestDetail(@PathVariable("id")Long id, Model viewModel){
        viewModel.addAttribute("title", "Dettaglio richiesta");
        viewModel.addAttribute("request", careerRequestService.find(id));
        return "career/requestDetail";

    }

    // rotta per l'accettazione di una richiesta 
    @PostMapping("/career/request/accept/{reqestId}")
    public String careerRequestAccept(@PathVariable Long requestId, RedirectAttributes redirectAttributes){
        careerRequestService.careerAccept(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "Ruolo abilitato per l'utente");

        return "redirect:/admin/dashboard";
    }

}
