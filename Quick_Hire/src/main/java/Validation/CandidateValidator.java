/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Validation;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author tejan
 */

@FacesValidator("candidateValidator")
public class CandidateValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context,
                         UIComponent component,
                         Object value) throws ValidatorException {

        if (value == null || value.toString().trim().isEmpty()) {
            return;
        }
        String type = (String) component.getAttributes().get("type");

        if (type == null) {
            return;
        }

        switch (type) {

            case "fullName":
                validateFullName(value);
                break;

            case "phone":
                validatePhone(value);
                break;

            case "city":
                validateCity(value);
                break;

            case "state":
                validateState(value);
                break;

            case "dob":
                validateDOB(value);
                break;

//            case "educationName":
//                validateEducationName(value);
//                break;

//            case "institute":
//                validateInstitute(value);
//                break;
//
//            case "specialization":
//                validateSpecialization(value);
//                break;

            case "grade":
                validateGrade(value);
                break;

            case "percentage":
                validatePercentage(value);
                break;

            case "cgpa":
                validateCGPA(value);
                break;
        }
    }

    private void validateFullName(Object value) {

        String name = value.toString().trim();

        if (name.length() < 2 || name.length() > 100) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Full name must be between 2 and 100 characters",
                    null));
        }

        if (!name.matches("^[A-Za-z ]+$")) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Only letters and spaces are allowed",
                    null));
        }
    }

    private void validatePhone(Object value) {

        String phone = value.toString();

        if (!phone.matches("^[6-9][0-9]{9}$")) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Enter valid 10-digit mobile number",
                    null));
        }
    }

    private void validateCity(Object value) {

        String city = value.toString().trim();

        if (city.length() < 2 || city.length() > 100) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "City must be between 2 and 100 characters",
                    null));
        }

        if (!city.matches("^[A-Za-z ]+$")) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "City can contain only letters",
                    null));
        }
    }

    private void validateState(Object value) {

        String state = value.toString().trim();

        if (state.length() < 2 || state.length() > 100) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "State must be between 2 and 100 characters",
                    null));
        }

        if (!state.matches("^[A-Za-z ]+$")) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "State can contain only letters",
                    null));
        }
    }

    private void validateDOB(Object value) {

        if (value == null) {
            return;
        }

        Date dob = (Date) value;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -18);

        if (dob.after(cal.getTime())) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Candidate must be at least 18 years old",
                    null));
        }
    }

//    private void validateEducationName(Object value) {
//
//        String education = value.toString().trim();
//
//        if (education.length() < 2 || education.length() > 150) {
//            throw new ValidatorException(
//                new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    "Degree name must be between 2 and 150 characters",
//                    null));
//        }
//
//        if (!education.matches("^[A-Za-z0-9 .,/()-]+$")) {
//            throw new ValidatorException(
//                new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    "Degree name contains invalid characters",
//                    null));
//        }
//    }

//    private void validateInstitute(Object value) {
//
//        String institute = value.toString().trim();
//
//        if (institute.length() < 2 || institute.length() > 200) {
//            throw new ValidatorException(
//                new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    "Institute name must be between 2 and 200 characters",
//                    null));
//        }
//        
//        if (!institute.matches("^[A-Za-z0-9 .,/()-]+$")) {
//            throw new ValidatorException(
//                new FacesMessage(
//                    FacesMessage.SEVERITY_ERROR,
//                    "Institute name contains invalid characters",
//                    null));
//        }
//    }
//
//    private void validateSpecialization(Object value) {
//
//        String specialization = value.toString().trim();
//
//        if (specialization.length() > 150) {
//            throw new ValidatorException(
//                new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    "Specialization cannot exceed 150 characters",
//                    null));
//        }
//    }

    private void validateGrade(Object value) {

        String grade = value.toString().trim();

        if (grade.length() > 20) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Grade cannot exceed 20 characters",
                    null));
        }
    }

    private void validatePercentage(Object value) {

        Double percentage = Double.valueOf(value.toString());

        if (percentage < 0 || percentage > 100) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Percentage must be between 0 and 100",
                    null));
        }
    }

    private void validateCGPA(Object value) {

        Double cgpa = Double.valueOf(value.toString());

        if (cgpa < 0 || cgpa > 10) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "CGPA must be between 0 and 10",
                    null));
        }
    }
}
