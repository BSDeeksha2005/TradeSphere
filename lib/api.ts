const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export class AuthError extends Error {
  constructor(message = "Session expired") {
    super(message);
    this.name = "AuthError";
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
  });

  if (response.status === 401 || response.status === 403) {
    throw new AuthError();
  }

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || "Something went wrong. Please try again.");
  }

  return response.json();
}

function authHeader(token: string): HeadersInit {
  return { Authorization: `Bearer ${token}` };
}

// ------------------------------------------------------------
// AUTH — unchanged behavior
// ------------------------------------------------------------
interface LoginSuccessBody {
  id: string;
  name: string;
  email: string;
  role: string;
  balance: number;
  token: string;
}

export function loginUser(email: string, password: string): Promise<LoginSuccessBody> {
  return request<LoginSuccessBody>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

interface RegisterSuccessBody {
  id: string;
  name: string;
  email: string;
  role: string;
  balance: number;
}

export function registerUser(
  name: string,
  email: string,
  password: string
): Promise<RegisterSuccessBody> {
  return request<RegisterSuccessBody>("/api/auth/register", {
    method: "POST",
    body: JSON.stringify({ name, email, password }),
  });
}

// ------------------------------------------------------------
// DASHBOARD DATA — unchanged behavior, refactored onto `request`
// ------------------------------------------------------------
export interface Account {
  id: string;
  name: string;
  email: string;
  role: string;
  balance: number;
}

export interface Asset {
  id: string;
  symbol: string;
  name: string;
  price: number;
}

export interface PortfolioHolding {
  id: string;
  userId: string;
  symbol: string;
  quantity: number;
  averagePrice: number;
}

export interface Transaction {
  id: string;
  userId: string;
  symbol: string;
  type: "BUY" | "SELL";
  quantity: number;
  price: number;
  totalValue: number;
  timestamp: string;
}

export function getAccount(token: string): Promise<Account> {
  return request<Account>("/api/account", { headers: authHeader(token) });
}

export function getAssets(token: string): Promise<Asset[]> {
  return request<Asset[]>("/api/assets", { headers: authHeader(token) });
}

export function getPortfolio(token: string): Promise<PortfolioHolding[]> {
  return request<PortfolioHolding[]>("/api/portfolio", { headers: authHeader(token) });
}

export function getTransactions(token: string): Promise<Transaction[]> {
  return request<Transaction[]>("/api/transactions", { headers: authHeader(token) });
}

// ------------------------------------------------------------
// TRADING — new
// ------------------------------------------------------------
export interface BuyResult {
  symbol: string;
  quantity: number;
  pricePerShare: number;
  totalCost: number;
  remainingBalance: number;
}

export function buyStock(token: string, symbol: string, quantity: number): Promise<BuyResult> {
  return request<BuyResult>("/api/portfolio/buy", {
    method: "POST",
    headers: authHeader(token),
    body: JSON.stringify({ symbol, quantity }),
  });
}

export interface SellResult {
  symbol: string;
  quantity: number;
  pricePerShare: number;
  totalValue: number;
  remainingBalance: number;
}

export function sellStock(token: string, symbol: string, quantity: number): Promise<SellResult> {
  return request<SellResult>("/api/portfolio/sell", {
    method: "POST",
    headers: authHeader(token),
    body: JSON.stringify({ symbol, quantity }),
  });
}