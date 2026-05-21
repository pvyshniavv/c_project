package GUI_Project.view;

import GUI_Project.model.OutputGraph;

public interface MainView {
    void showLoading(boolean isLoading);

    void showError(String errorMessage);

    void displayGraph(OutputGraph graph);
}
