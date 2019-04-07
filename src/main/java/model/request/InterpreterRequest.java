package model.request;

import model.Node;


public class InterpreterRequest extends Request{

    public enum Language{
        SPANISH, FRENCH, MANDARIN, ENGLISH;
    }



    private Language l;

    public InterpreterRequest(int id, String notes, Node location, boolean completed, Language l){
        super(id, notes, location, completed);
        this.l = l;
    }

    public Language getLanguageType(){
        return l;
    }

    public void setLanguage(Language l){
        this.l = l;
    }

    @Override
    public void makeRequest() {

    }

    @Override
    public void fillRequest() {

    }
}
