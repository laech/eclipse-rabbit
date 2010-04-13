package rabbit.ui.internal.actions;

public interface ICategoryProvider {
  
  ICategory[] getUnselectedCategories();

  ICategory[] getSelectedCategories();
  
  void setSelectedCategories(ICategory... categories);
}
