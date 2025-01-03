import { makeAutoObservable } from "mobx";
import { createCategory, CustomCategory, deleteCategory, getAllCategories } from "../api/goalsServices";

class CategoriesStore {
    categories: CustomCategory[] = [];

    constructor() {
        makeAutoObservable(this);
        this.getAllCategoriesAction();
    }

    getAllCategoriesAction = async () => {
        const data = await getAllCategories();
        this.categories = data;
    };

    createCategoryAction = async (category: CustomCategory) => {
        const newCategory = await createCategory(category);
        this.categories.push(newCategory);
    };

    deleteCategoryAction = async (categoryId: string) => {
        await deleteCategory(categoryId);
        this.categories = this.categories.filter((category) => category.id !== categoryId);
    };

    get initialCategoryVisibility() {
        return this.categories.reduce(
            (map, category) => {
                map[category.id] = true;
                return map;
            },
            {} as Record<string, boolean>,
        );
    }
}

const categoriesStore = new CategoriesStore();
export default categoriesStore;
