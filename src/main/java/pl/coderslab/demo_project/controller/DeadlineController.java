package pl.coderslab.demo_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.demo_project.calendar.DeadlineEvent;
import pl.coderslab.demo_project.entity.Deadline;
import pl.coderslab.demo_project.entity.Patient;
import pl.coderslab.demo_project.repository.DeadlineRepository;
import pl.coderslab.demo_project.repository.PatientRepository;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Controller
@RequestMapping("/deadline")
public class DeadlineController {

    private final DeadlineRepository deadlineRepository;
    private final PatientRepository patientRepository;
    private final DeadlineEvent deadlineEvent;

    private static final String form = "/deadline/form";

    public DeadlineController(DeadlineRepository deadlineRepository, PatientRepository patientRepository) {
        this.deadlineRepository = deadlineRepository;
        this.patientRepository = patientRepository;
        this.deadlineEvent = new DeadlineEvent();
    }

    @GetMapping("/add/{patientId}")
    public String createForm(Model model){
        model.addAttribute("deadline", new Deadline());
        return form;
    }

    @PostMapping("/add/{patientId}")
    public String create(@Valid Deadline deadline, BindingResult result, @PathVariable Long patientId) throws Exception {
        if(result.hasErrors()){
            return form;
        }
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        Patient patient = patientOptional.orElseThrow(Exception::new);
        deadline.setPatient(patient);
        deadlineEvent.addEvent(deadline);
        deadlineRepository.save(deadline);
        return "redirect:/patient/" + patientId;
    }

    @GetMapping("/edit/{id}")
    public String editForm(Model model, @PathVariable long id){
        model.addAttribute("deadline", deadlineRepository.findById(id));
        return form;
    }

    @PostMapping("/edit/{id}")
    public String edit(@Valid Deadline deadline, BindingResult result) throws IOException, GeneralSecurityException {
        if(result.hasErrors()){
            return form;
        }
        deadlineEvent.editEvent(deadline);
        deadlineRepository.save(deadline);
        Long patientId = deadline.getPatient().getId();
        return "redirect:/patient/" + patientId;
    }

    @GetMapping("/delete/{id}")
    public String deleteView() {
        return "/delete";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable long id, @RequestParam String delete) throws Exception {
        Optional<Deadline> deadlineOptional = deadlineRepository.findById(id);
        Deadline deadline = deadlineOptional.orElseThrow(Exception::new);
        Long patientId = deadline.getPatient().getId();
        if (delete.equals("Delete")){
            deadlineEvent.deleteEvent(deadline);
            deadlineRepository.delete(deadline);
        }
        return "redirect:/patient/" + patientId;
    }

}
