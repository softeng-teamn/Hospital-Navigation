package model.request;

import model.Node;
import service.DatabaseService;

import java.util.Objects;


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
    public String toString() {
        return "InterpreterRequest{" + "Language= " + l.name() + '}';
    }

    @Override
    public void makeRequest() {
        DatabaseService.getDatabaseService().insertInterpreterRequest(this);
    }

    @Override
    public void fillRequest() {
        this.setCompleted(true);
        DatabaseService.getDatabaseService().updateInterpreterRequest(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InterpreterRequest that = (InterpreterRequest) o;
        return l == that.l;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), l);
    }
}
