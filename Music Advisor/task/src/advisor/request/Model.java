package advisor.request;

import java.util.List;
import java.util.ArrayList;

public class Model<T> {

    private Integer pageSize = 5;

    private List<T> content;

    private Integer currentPage;

    private Integer numberOfPages;

    public Model(Integer pageSize) {
        this.pageSize = pageSize;
        content = new ArrayList<T>(pageSize);
    }

    public void setContent(List<T> newContent) {
        content.clear();
        content.addAll(newContent);
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    @java.lang.Override
    public java.lang.String toString() {
        StringBuilder builder = new StringBuilder();
        content.forEach(c -> builder.append(c.toString()).append("\n"));
        builder.append("---PAGE ")
                .append(currentPage)
                .append(" OF ")
                .append(numberOfPages)
                .append("---");
        return builder.toString();
    }
}
