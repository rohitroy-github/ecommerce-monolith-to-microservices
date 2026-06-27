import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

const getStorageKey = (userId) => `cart_items_${userId || "guest"}`;

export const CartProvider = ({ children }) => {
  const { user } = useAuth();
  const userId = user?.userId ?? user?.id ?? null;
  const storageKey = useMemo(() => getStorageKey(userId), [userId]);

  const [items, setItems] = useState([]);

  useEffect(() => {
    try {
      const raw = localStorage.getItem(storageKey);
      setItems(raw ? JSON.parse(raw) : []);
    } catch {
      setItems([]);
    }
  }, [storageKey]);

  useEffect(() => {
    localStorage.setItem(storageKey, JSON.stringify(items));
  }, [items, storageKey]);

  const addToCart = (product) => {
    if (!product?.id) {
      return;
    }

    setItems((current) => {
      const existing = current.find((item) => item.id === product.id);

      if (existing) {
        return current.map((item) =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item,
        );
      }

      return [
        ...current,
        {
          id: product.id,
          name: product.name,
          description: product.description,
          price: Number(product.price) || 0,
          sellerId: product.sellerId,
          quantity: 1,
        },
      ];
    });
  };

  const updateQuantity = (productId, quantity) => {
    const parsed = Number(quantity);
    if (!Number.isInteger(parsed) || parsed < 1) {
      return;
    }

    setItems((current) =>
      current.map((item) =>
        item.id === productId ? { ...item, quantity: parsed } : item,
      ),
    );
  };

  const removeFromCart = (productId) => {
    setItems((current) => current.filter((item) => item.id !== productId));
  };

  const clearCart = () => {
    setItems([]);
  };

  const itemCount = items.reduce((total, item) => total + item.quantity, 0);

  const value = useMemo(
    () => ({
      items,
      itemCount,
      addToCart,
      updateQuantity,
      removeFromCart,
      clearCart,
    }),
    [items, itemCount],
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

export const useCart = () => {
  const context = useContext(CartContext);

  if (!context) {
    throw new Error("useCart must be used within CartProvider.");
  }

  return context;
};
