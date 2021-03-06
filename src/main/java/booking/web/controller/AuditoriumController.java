package booking.web.controller;

import booking.domain.Auditorium;
import booking.service.AuditoriumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static booking.web.controller.AuditoriumController.ENDPOINT;

@Controller
@SuppressWarnings("unused")
@RequestMapping(value = ENDPOINT)
public class AuditoriumController {
    public static final String ENDPOINT = "/auditorium";
    private static final String AUDITORIUMS_ATTR = "auditoriums";
    private static final String AUDITORIUM_ATTR = "auditorium";

    private static final String AUDITORIUMS_FTL = "auditorium/auditoriums";
    private static final String AUDITORIUM_FTL = "auditorium/auditorium";
    private static final String AUDITORIUM_SEATS_NUMBER_FTL = "auditorium/auditorium_seats_number";
    private static final String AUDITORIUM_VIP_SEATS_FTL = "auditorium/auditorium_vip_seats";
    private static final String AUDITORIUM_DELETED_FTL = "auditorium/auditorium_deleted";
    private static final String AUDITORIUM_CREATED_FTL = "auditorium/auditorium_created";

    private final AuditoriumService auditoriumService;

    @Autowired
    public AuditoriumController(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    String create(
            @RequestParam String auditoriumName,
            @RequestParam int seatsNumber,
            @RequestParam String vipSeats,
            @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        List<Integer> vipSeatList = SeatHelper.parseSeatsString(vipSeats);
        Auditorium auditorium = auditoriumService.create(new Auditorium(auditoriumName, seatsNumber, vipSeatList));
        model.addAttribute(AUDITORIUM_ATTR, auditorium);
        return AUDITORIUM_CREATED_FTL;
    }

    @RequestMapping(method = RequestMethod.GET)
    String getAll(@ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        List<Auditorium> auditoriums = auditoriumService.getAll();
        model.addAttribute(AUDITORIUMS_ATTR, auditoriums);
        return AUDITORIUMS_FTL;
    }

    @RequestMapping("/{auditoriumId}")
    String getById(@PathVariable Long auditoriumId, @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Auditorium auditorium = auditoriumService.getById(auditoriumId);
        model.addAttribute(AUDITORIUM_ATTR, auditorium);
        return AUDITORIUM_FTL;
    }

    @RequestMapping("/{auditoriumId}/seatsNumber")
    String getSeatsNumber(@PathVariable Long auditoriumId, @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Auditorium auditorium = auditoriumService.getById(auditoriumId);
        model.addAttribute(AUDITORIUM_ATTR, auditorium);
        return AUDITORIUM_SEATS_NUMBER_FTL;
    }

    @RequestMapping("/{auditoriumId}/vipSeats")
    String getVipSeats(@PathVariable Long auditoriumId, @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Auditorium auditorium = auditoriumService.getById(auditoriumId);
        model.addAttribute(AUDITORIUM_ATTR, auditorium);
        return AUDITORIUM_VIP_SEATS_FTL;
    }

    @RequestMapping(path = "/{auditoriumId}/delete", method = RequestMethod.DELETE)
    String delete(@PathVariable Long auditoriumId, @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Auditorium auditorium = auditoriumService.getById(auditoriumId);
        auditoriumService.delete(auditoriumId);
        model.addAttribute(AUDITORIUM_ATTR, auditorium);
        return AUDITORIUM_DELETED_FTL;
    }
}
