export interface CartResponse {
    items: Array<{
      id: number;
      itemCosts: number;
      productName: string | null;
      quantity: number;
      shopCode: string;
      status: string;
    }>;
    total: number;
  }