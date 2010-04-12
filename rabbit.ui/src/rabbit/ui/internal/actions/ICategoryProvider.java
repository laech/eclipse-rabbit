package rabbit.ui.internal.actions;

public interface ICategoryProvider {

  ICategory[] getCategories();
  
  void setCategories(ICategory[] categories);
}
