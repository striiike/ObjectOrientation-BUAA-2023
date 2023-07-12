package book;

import java.util.Objects;

public class Book {
    private final String category;
    private final String serialNum;
    private boolean lentOut;
    private String school;
    private boolean isSmeared = false;

    public String getCategory() {
        return category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, serialNum);
    }

    public String getSerialNum() {
        return serialNum;
    }

    public Book(String category, String serialNum, String lent, String school) {
        this.category = category;
        this.serialNum = serialNum;
        this.lentOut = Objects.equals(lent, "Y");
        this.school = school;
    }

    @Override
    public boolean equals(Object book) {
        if (book instanceof Book) {
            return Objects.equals(((Book) book).getCategory(), category) &&
                    Objects.equals(((Book) book).getSerialNum(), serialNum);
        }
        return false;
    }

    @Override
    public String toString() {
        return school + "-" + category + "-" + serialNum;
    }

    public String toLessString() {
        return category + "-" + serialNum;
    }

    public void setSmeared(boolean smeared) {
        this.isSmeared = smeared;
    }

    public boolean isSmeared() {
        return this.isSmeared;
    }

    public boolean canLentOut() {
        return this.lentOut;
    }

    public String getSchool() {
        return this.school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setLentOut(boolean lentOut) {
        this.lentOut = lentOut;
    }
}
