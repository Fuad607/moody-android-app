
package com.example.moody.model;

import java.util.List;
public class SurveyResponse {

    private List<Result> result = null;
    private List<String> labelDate = null;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public List<String> getLabelDate() {
        return labelDate;
    }

    public void setLabelDate(List<String> labelDate) {
        this.labelDate = labelDate;
    }

}
