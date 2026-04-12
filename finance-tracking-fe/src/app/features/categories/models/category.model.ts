export interface SubCategory {
  id: string;
  code: string;
  displayName: string;
}

export interface Category {
  id: string;
  code: string;
  displayName: string;
  subCategories: SubCategory[];
  createdAt: string;
  updatedAt: string;
}
