package cz.czechitas.java2webapps.ukol6.controller;

import cz.czechitas.java2webapps.ukol6.entity.Vizitka;
import cz.czechitas.java2webapps.ukol6.repository.VizitkaRepository;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class VizitkaController {

  private final VizitkaRepository vizitkaRepository; // field pro repository

  //konstruktor, který dostane repository jako vstupní paramentr a uloží si ho do fieldu,
  // aby bylo možné později repository v controlleru používat
  public VizitkaController(VizitkaRepository repository, VizitkaRepository vizitkaRepository) {
    this.vizitkaRepository = vizitkaRepository;
  }

  // tento kód zajistí, aby se prázdné stringy převedly na null
  @InitBinder
  public void nullStringBinding(WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }


  // zobrazí seznam vizitek - view seznam
  @GetMapping("/")
  public Object seznam() {
    return new ModelAndView("seznam")
      .addObject("vizitky", vizitkaRepository.findAll());

  }

  // bod 12 - reaguje na požadavky metodou GET, které budou mít v URL hned za lomítkem číslo
  @GetMapping("/{id:[0-9]+}") // v url za lomítkem bude číslo
  public Object vizitka(@PathVariable int id) { // v příkladu z lekce 8 se jedná o metodu detail
    Optional<Vizitka> vizitka = vizitkaRepository.findById(id);
    if (vizitka.isEmpty()) { // ověřím, zda je přítomná hodnota
      return ResponseEntity.notFound().build(); //  pokud vizitka s daným id neexistuje, ukončím s kódem 404
    }
    return new ModelAndView("vizitka") // pokud id existuje, vložíme jí do modelu, zobrazíme pomocí šablony vizitka ftlh
      .addObject("vizitka", vizitka.get());
    }


  // bod 15 - kliknutím na tlačítko nová vizitka se zobrazí prázdná vizitka (01:21)
  @GetMapping("/nova")
  public Object nova() {
    return new ModelAndView("formular")
      .addObject("vizitka", new Vizitka());
  }

  // bod 15 - formulář odešle data metodou Post na adresu /nova
  // podívat se do vzorového řešení lekce 8
  // název metody ???
  @PostMapping("/nova")
  public Object pridat(@ModelAttribute("vizitka") @Valid Vizitka vizitka, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "formular";
    }
        vizitkaRepository.save(vizitka); // přidáme entitu, kterou chceme uložit - bod 16

  return "redirect:/"; // po přidání vizitky se vrátím na hlavní stránku se seznamem vizitek
  }
}
