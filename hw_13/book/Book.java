package book;

import java.util.Objects;

public class Book {
    private final String category;
    private final String serialNum;

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

    public Book(String category, String serialNum) {
        this.category = category;
        this.serialNum = serialNum;
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
        return category + "-" + serialNum;
    }

    public void setSmeared(boolean smeared) {
        this.isSmeared = smeared;
    }

    public boolean isSmeared() {
        return this.isSmeared;
    }

}
