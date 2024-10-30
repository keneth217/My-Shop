export interface SalesResponse {
    content: Array<{
      id: number;
      customerAddress: string | null; // Allowing for null values
      customerName: string | null; // Allowing for null values
      customerPhone: string; // Changing to string based on provided example
      salePerson: string;
      date: string; // Assuming this is in ISO format or a standard date string
      totalPrice: number;
      shopCode: string; // Added shopCode as it was present in the data
    }>;
    total: number; // Total number of items (not just the ones in content)
    number: number; // Current page number
    size: number; // Size of the page
    totalElements: number; // Total elements available across all pages
    totalPages: number; // Total number of pages
  }