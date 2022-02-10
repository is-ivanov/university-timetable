package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.ui.PageSequenceCreator;
import ua.com.foxminded.university.ui.util.MappingConstants;

import javax.servlet.http.HttpServletRequest;

import static ua.com.foxminded.university.ui.util.ResponseUtil.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(MappingConstants.FACULTIES)
public class FacultyController {

    private final FacultyService facultyService;
    private final PageSequenceCreator pageSequenceCreator;

    @GetMapping
    public String showFaculties(Model model,
                                @PageableDefault(sort = "name") Pageable pageable) {
        log.debug("Getting data for faculty.html");
        Page<Faculty> pageFaculties = facultyService.findAll(pageable);
        model.addAttribute("faculties", pageFaculties.getContent());
        model.addAttribute("page", pageFaculties);
        model.addAttribute("uri", MappingConstants.FACULTIES);
        model.addAttribute("newFaculty", new Faculty());
        model.addAttribute("pages", pageSequenceCreator
            .createPageSequence(pageFaculties.getTotalPages(),
                pageFaculties.getNumber() + 1));
        log.debug("The required data is loaded into the model");
        return "faculty";
    }

    @DeleteMapping(MappingConstants.ID)
    public String deleteFaculty(@PathVariable("id") int facultyId,
                                HttpServletRequest request) {
        log.debug("Deleting faculty with id({})", facultyId);
        facultyService.delete(facultyId);
        log.debug("Faculty id({}) is deleted", facultyId);
        return defineRedirect(request);
    }

}
