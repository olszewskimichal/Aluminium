package com.zespolowka.controller;

import com.zespolowka.entity.createTest.Test;
import com.zespolowka.entity.solutionTest.SolutionStatus;
import com.zespolowka.entity.solutionTest.SolutionTest;
import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.CreateTestForm;
import com.zespolowka.forms.ProgrammingTaskForm;
import com.zespolowka.forms.TaskForm;
import com.zespolowka.service.SolutionTestService;
import com.zespolowka.service.TestFormService;
import com.zespolowka.service.TestService;
import com.zespolowka.service.UserService;
import com.zespolowka.validators.CreateTestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    private final TestFormService testFormService;
    private final TestService testService;
    private final CreateTestValidator createTestValidator;
    private final SolutionTestService solutionTestService;
    private final UserService UserService;
    private CreateTestForm createTestForm;

    @Autowired
    public TestController(final TestFormService testFormService, TestService testService, CreateTestValidator createTestValidator, SolutionTestService solutionTestService, UserService UserService) {
        this.testFormService = testFormService;
        this.testService = testService;
        this.createTestValidator = createTestValidator;
        this.solutionTestService = solutionTestService;
        this.UserService = UserService;
    }


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "create")
    public String createTest(final Model model) {
        logger.info("Metoda - createTest");
        createTestForm = this.testFormService.getTestFromSession();
        model.addAttribute("createTestForm", createTestForm);
        return "tmpCreateTest";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/edit/{id}")
    public String editTest(@PathVariable("id") Long id, final Model model) {
        logger.info("Metoda - editTest");
        if (testFormService.getEditTestIdFromSession() != null) {
            createTestForm = testFormService.getEditTestFromSession();
        } else {
            createTestForm = testService.createForm(testService.getTestById(id));
            testFormService.setEditTestIdInSession(id);
            testFormService.updateEditTestFormInSession(createTestForm);
        }
        model.addAttribute("createTestForm", createTestForm);
        return "editTest";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "create/add", method = RequestMethod.POST)
    public String addQuestion(@RequestParam(value = "questionId", defaultValue = "0") int questionId, final CreateTestForm createTestForm) {
        testFormService.updateTestFormInSession(createTestForm);
        logger.info("Metoda - addQuestion");
        switch (questionId) {
            case 0: {
                testFormService.addTaskFormToTestForm(new TaskForm(TaskForm.CLOSEDTASK));
                break;
            }
            case 1: {
                testFormService.addTaskFormToTestForm(new TaskForm(TaskForm.OPENTASK));
                break;
            }
            case 2: {
                testFormService.addTaskFormToTestForm(new TaskForm(TaskForm.PROGRAMMINGTASK));
                break;
            }
            case 3: {
                testFormService.addTaskFormToTestForm(new TaskForm(TaskForm.SQLTASK));
                break;
            }
        }

        return "redirect:/test/create";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "edit/add", method = RequestMethod.POST)
    public String addEditQuestion(@RequestParam(value = "questionId", defaultValue = "0") int questionId, final CreateTestForm createTestForm) {
        testFormService.updateEditTestFormInSession(createTestForm);
        logger.info("Metoda - addEditQuestion");
        switch (questionId) {
            case 0: {
                testFormService.addTaskFormToEditTestForm(new TaskForm(TaskForm.CLOSEDTASK));
                break;
            }
            case 1: {
                testFormService.addTaskFormToEditTestForm(new TaskForm(TaskForm.OPENTASK));
                break;
            }
            case 2: {
                testFormService.addTaskFormToEditTestForm(new TaskForm(TaskForm.PROGRAMMINGTASK));
                break;
            }
            case 3: {
                testFormService.addTaskFormToEditTestForm(new TaskForm(TaskForm.SQLTASK));
                break;
            }
        }

        return "redirect:/test/edit/" + testFormService.getEditTestIdFromSession();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "create/change", method = RequestMethod.POST)
    public String changeLanguages(@RequestParam(value = "taskId", defaultValue = "0") Integer taskId, @RequestParam(value = "selected", defaultValue = "java") String selected, final CreateTestForm createTestForm, final Model model) {
        logger.info("Metoda - changeLanguages");
        taskId -= 1;
        testFormService.updateSelectedLanguagesInSession(selected);
        String languages[] = selected.split(",");
        TaskForm taskForm = createTestForm.getTasks().get(taskId);
        Set<ProgrammingTaskForm> programmingTaskFormSet = taskForm.getProgrammingTaskForms();
        Set<String> lang = new HashSet<>(Arrays.asList(languages));
        taskForm.setLanguages(lang);
        taskForm.setProgrammingTaskForms(
                testFormService.createProgrammingTaskSet(programmingTaskFormSet, languages, taskForm));

        createTestForm.getTasks().set(taskId, taskForm);
        testFormService.updateTestFormInSession(createTestForm);

        return "redirect:/test/create";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "edit/change", method = RequestMethod.POST)
    public String changeEditLanguages(@RequestParam(value = "taskId", defaultValue = "0") Integer taskId, @RequestParam(value = "selected", defaultValue = "java") String selected, final CreateTestForm createTestForm, final Model model) {
        logger.info("Metoda - changeEditLanguages");
        testFormService.updateSelectedLanguagesInSession(selected);
        String languages[] = selected.split(",");
        TaskForm taskForm = createTestForm.getTasks().get(taskId);
        Set<ProgrammingTaskForm> programmingTaskFormSet = taskForm.getProgrammingTaskForms();
        Set<String> lang = new HashSet<>(Arrays.asList(languages));
        taskForm.setLanguages(lang);
        taskForm.setProgrammingTaskForms(
                testFormService.createProgrammingTaskSet(programmingTaskFormSet, languages, taskForm));

        createTestForm.getTasks().set(taskId, taskForm);
        testFormService.updateEditTestFormInSession(createTestForm);

        return "redirect:/test/edit/" + testFormService.getEditTestIdFromSession();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "create/remove", method = RequestMethod.POST)
    public String removeQuestion(@RequestParam(value = "taskId") int taskId, final CreateTestForm createTestForm, final Model model) {
        logger.info("removeQuestion");
        createTestForm.getTasks().remove(taskId);
        testFormService.updateTestFormInSession(createTestForm);

        return "redirect:/test/create";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "edit/remove", method = RequestMethod.POST)
    public String removeEditQuestion(@RequestParam(value = "taskId") int taskId, final CreateTestForm createTestForm, final Model model) {
        logger.info("removeEditQuestion");
        createTestForm.getTasks().remove(taskId);
        testFormService.updateEditTestFormInSession(createTestForm);

        return "redirect:/test/edit/" + testFormService.getEditTestIdFromSession();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String save(final @Valid CreateTestForm createTestForm, final BindingResult result, final RedirectAttributes redirectAttributes) {
        logger.info("Metoda - save");
        createTestValidator.validate(createTestForm, result);
        if (result.hasErrors()) {
            logger.info(result.getAllErrors().toString());
            return "tmpCreateTest";
        }
        testFormService.updateTestFormInSession(createTestForm);
        Test test = testService.create(createTestForm);
        logger.info(test.toString());
        testFormService.updateTestFormInSession(new CreateTestForm());
        testFormService.updateSelectedLanguagesInSession("");
        redirectAttributes.addFlashAttribute("sukces", true);
        return "redirect:/test/create";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String saveEdit(final @Valid CreateTestForm createTestForm, final BindingResult result) {
        logger.info("Metoda - save");
        createTestValidator.validate(createTestForm, result);
        if (result.hasErrors()) {
            logger.info(result.getAllErrors().toString());
            return "editTest";
        }
        testService.update(createTestForm, testFormService.getEditTestIdFromSession());
        testFormService.setEditTestIdInSession(null);
        testFormService.updateEditTestFormInSession(null);
        return "redirect:/test/all";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/delete/{id}")
    public String deleteTest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Metoda - deleteTest");
        testService.delete(id);
        redirectAttributes.addFlashAttribute("deleted", true);
        return "redirect:/test/all";
    }


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping("/all")
    public String showAll(Model model) {
        logger.info("metoda - showAll");
        try {
            Map<Test, Integer> testMap = new HashMap<>();
            ArrayList<Test> lista = new ArrayList<>(testService.getAllTests());
            for (Test aLista : lista)
                testMap.put(aLista,
                        solutionTestService.countSolutionTestsByTestAndSolutionStatus(aLista, SolutionStatus.FINISHED));

            model.addAttribute("Tests", testMap);
            testFormService.removeEditTestIdInSession();
            testFormService.removeEditTestFormInSession();

        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
        return "tests";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showUserTests(@PathVariable final Long id, final Model model) {
        logger.info("nazwa metody = showUserTests");
        try {
            User user = UserService.getUserById(id)
                    .orElseThrow(
                            () -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", id)));
            model.addAttribute("Tests", solutionTestService.getSolutionTestsByUser(user));
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("{}\n{}", id.toString(), model);
        }
        return "userTests";
    }

    @RequestMapping("/pdf/{id}")
    public void getPDF(@PathVariable final Long id, HttpServletRequest request,
                       HttpServletResponse response) {

        Collection<SolutionTest> tests = solutionTestService.getSolutionTestsByTest(testService.getTestById(id));
        final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String[] header = new String[5];
        header[0] = "Lp";
        header[1] = "Osoba";
        header[2] = "Wynik testu";
        header[3] = "%";
        header[4] = "Data rozwiązania testu";

        String[][] body = new String[tests.size()][5];
        int i = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (tests.size() > 0) {
            String title = "Raport z dnia: " + LocalDate.now() + "\nDla: " + tests.iterator()
                    .next()
                    .getTest()
                    .getName() + "\n\n";
            for (SolutionTest test : tests) {
                body[i][0] = "" + (i + 1);
                body[i][1] = "" + test.getUser().getName() + " " + test.getUser().getLastName() + ", " + test.getUser()
                        .getEmail();
                body[i][2] = "" + test.getPoints() + " / " + test.getTest().getMaxPoints();
                BigDecimal procent = BigDecimal.valueOf(test.getPoints() / test.getTest().getMaxPoints() * 100);
                body[i][3] = "" + procent.setScale(2, RoundingMode.HALF_UP).floatValue() + " %";
                body[i][4] = "" + formatter.format(test.getEndSolution());
                i++;
            }

            String filePath = "RaportDla" + currentUser.getUser().getId() + ".pdf";
            ServletContext context = request.getServletContext();
            String appPath = context.getRealPath("");
            String fullPath = appPath + filePath.replaceAll(" ", "");
            logger.info("PDF utworzony w: {}", fullPath);
            File file = new File(fullPath.replaceAll(" ", ""));
            testService.createPDF(file, title, header, body);

            try {
                FileInputStream inputStream = new FileInputStream(file);
                response.setContentType("application/pdf");
                org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();

                //METODA POBRANIA PDF
                /*
                String mimeType = context.getMimeType(fullPath);
                if (mimeType == null) {
                    // set to binary type if MIME mapping not found
                    mimeType = "application/octet-stream";
                }
                // set content attributes for the response
                response.setContentType(mimeType);
                response.setContentLength((int) file.length());
                // set headers for the response
                String headerKey = "Content-Disposition";
                String headerValue = String.format("attachment; filename=\"%s\"",
                        file.getName());
                response.setHeader(headerKey, headerValue);
                // get output stream of the response
                OutputStream outStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                // write bytes read from the input stream into the output stream
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.close();
                */
                inputStream.close();
                Files.delete(file.toPath());
            } catch (Exception ex) {
                logger.info("FILE NOT FOUND: {}", ex.getMessage());
            }
        }
    }
}
