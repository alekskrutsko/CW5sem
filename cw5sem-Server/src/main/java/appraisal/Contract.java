package appraisal;

import org.apache.commons.math3.util.Precision;
import personalInfo.Appraiser;
import personalInfo.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Contract {
    private Integer contractID;
    private AppraisalObject appraisalObject;
    private Double priceForAppraisal;
    private Double expectedPrice;
    private Double appraiserPrice;
    private String dateOfSigning;
    private String commentFromConsumer;
    private String commentFromAppraiser;
    private Status status;
    private String strStatus;
    private User consumer;
    private Appraiser appraiser;

    public void setAppraiser(Appraiser appraiser) {
        this.appraiser = appraiser;
    }

    public void setConsumer(User consumer) {
        this.consumer = consumer;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public Appraiser getAppraiser() {
        return appraiser;
    }

    public User getConsumer() {
        return consumer;
    }

    public enum Status {
        NEW(0),
        WAITFORCONSUMER(1),
        WAITFORAPPRAISER(2),
        TERMINATED(3),
        SIGNED(4);

        private int value;
        private static Map map = new HashMap<>();

        private Status(int value) {
            this.value = value;
        }

        static {
            for (Status status1 : Status.values()) {
                map.put(status1.value, status1);
            }
        }

        public static Status valueOf(int status) {
            return (Status) map.get(status);
        }

        public int getValue() {
            return value;
        }
    }

    private void setStrStatus(Status status) {
        if (status == Status.NEW) {
            strStatus = "Новый";
        } else if (status == Status.WAITFORCONSUMER) {
            strStatus = "Ожидание ответа заказчика";
        } else if (status == Status.WAITFORAPPRAISER) {
            strStatus = "Ожидание ответа оценщика";
        } else if (status == Status.TERMINATED) {
            strStatus = "Расторгнут";
        } else if (status == Status.SIGNED) {
            strStatus = "Заключен";
        }
    }

    public Integer getContractID() {
        return contractID;
    }

    public void setContractID(Integer contractID) {
        this.contractID = contractID;
    }

    public AppraisalObject getAppraisalObject() {
        return appraisalObject;
    }

    public void setAppraisalObject(AppraisalObject appraisalObject) {
        this.appraisalObject = appraisalObject;
    }

    public Double getPriceForAppraisal() {
        return priceForAppraisal;
    }

    public void setPriceForAppraisal(Double priceForAppraisal) {
        this.priceForAppraisal = priceForAppraisal;
    }

    public Double getExpectedPrice() {
        return expectedPrice;
    }

    public void setExpectedPrice(Double expectedPrice) {
        this.expectedPrice = expectedPrice;
    }

    public Double getAppraiserPrice() {
        return appraiserPrice;
    }

    public void setAppraiserPrice(Double appraiserPrice) {
        this.appraiserPrice = appraiserPrice;
    }

    public String getDateOfSigning() {
        return dateOfSigning;
    }

    public void setDateOfSigning(String dateOfSigning) {
        this.dateOfSigning = dateOfSigning;
    }


    public String getCommentFromConsumer() {
        return commentFromConsumer;
    }

    public void setCommentFromConsumer(String commentFromConsumer) {
        this.commentFromConsumer = commentFromConsumer;
    }

    public String getCommentFromAppraiser() {
        return commentFromAppraiser;
    }

    public void setCommentFromAppraiser(String commentFromAppraiser) {
        this.commentFromAppraiser = commentFromAppraiser;
    }

    public Status getStatus() {
        return status;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStatus(Status status) {
        this.status = status;
        setStrStatus(status);
    }

    public Contract(AppraisalObject appraisalObject, Double expectedPrice,
                    String commentFromConsumer, Status status, User consumer) {
        this.appraisalObject = appraisalObject;
        this.expectedPrice = expectedPrice;
        this.commentFromConsumer = commentFromConsumer;
        this.status = status;
        this.consumer = consumer;
        setStrStatus(status);
    }

    public Contract(Integer contractID, AppraisalObject appraisalObject, Double priceForAppraisal,
                    Double expectedPrice, Double appraiserPrice, String dateOfSigning,
                    String commentFromConsumer, String commentFromAppraiser,
                    Status status, User consumer, Appraiser appraiser) {
        this.contractID = contractID;
        this.appraisalObject = appraisalObject;
        this.priceForAppraisal = priceForAppraisal;
        this.expectedPrice = expectedPrice;
        this.appraiserPrice = appraiserPrice;
        this.dateOfSigning = dateOfSigning;
        this.commentFromConsumer = commentFromConsumer;
        this.commentFromAppraiser = commentFromAppraiser;
        this.status = status;
        this.consumer = consumer;
        this.appraiser = appraiser;
        setStrStatus(status);
    }

    public String getObjectName() {
        return appraisalObject.getName();
    }


    public Contract(Integer contractID, AppraisalObject appraisalObject, Double priceForAppraisal,
                    Double interestRate, String dateOfSigning,
                    String commentFromConsumer, String commentFromAppraiser, Status status, User consumer, Appraiser appraiser) {
        this.contractID = contractID;
        this.appraisalObject = appraisalObject;
        this.priceForAppraisal = priceForAppraisal;
        this.appraiserPrice = interestRate;
        this.dateOfSigning = dateOfSigning;
        this.commentFromConsumer = commentFromConsumer;
        this.commentFromAppraiser = commentFromAppraiser;
        this.consumer = consumer;
        this.appraiser = appraiser;
        this.status = status;
        setStrStatus(status);
    }
}