// API Base Configuration
const API_BASE_URL = 'http://localhost:8080/api';

class ApiClient {
    constructor() {
        this.baseUrl = API_BASE_URL;
    }

    getAuthToken() {
        return localStorage.getItem('token');
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const token = this.getAuthToken();
        
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token && !options.skipAuth) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const response = await fetch(url, {
                ...options,
                headers
            });

            if (response.status === 401) {
                // Token expired, redirect to login
                localStorage.removeItem('token');
                window.location.href = '/index.html';
                throw new Error('Unauthorized');
            }

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Request failed');
            }

            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // Authentication
    async login(username, password) {
        return this.request(`/users/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`, {
            method: 'POST',
            skipAuth: true
        });
    }

    async register(data) {
        return this.request('/users/register', {
            method: 'POST',
            body: JSON.stringify(data),
            skipAuth: true
        });
    }

    async forgotPassword(email) {
        return this.request(`/users/forgot-password?email=${encodeURIComponent(email)}`, {
            method: 'POST',
            skipAuth: true
        });
    }

    async resetPassword(token, newPassword) {
        return this.request(`/users/reset-password?token=${encodeURIComponent(token)}&newPassword=${encodeURIComponent(newPassword)}`, {
            method: 'POST',
            skipAuth: true
        });
    }

    // Dashboard
    async getDashboardStats() {
        return this.request('/stock/dashboard/stats');
    }

    async getLowStockItems() {
        return this.request('/stock/low-stock');
    }

    // Products
    async getProducts() {
        return this.request('/products');
    }

    async createProduct(product) {
        return this.request('/products', {
            method: 'POST',
            body: JSON.stringify(product)
        });
    }

    // Warehouses
    async getWarehouses() {
        return this.request('/warehouses');
    }

    async createWarehouse(warehouse) {
        return this.request('/warehouses', {
            method: 'POST',
            body: JSON.stringify(warehouse)
        });
    }

    // Suppliers
    async getSuppliers() {
        return this.request('/suppliers');
    }

    async createSupplier(supplier) {
        return this.request('/suppliers', {
            method: 'POST',
            body: JSON.stringify(supplier)
        });
    }

    // Stock Operations
    async createReceipt(data) {
        return this.request('/stock/receipt', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async createDelivery(data) {
        return this.request('/stock/delivery', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async createTransfer(data) {
        return this.request('/stock/transfer', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async createAdjustment(data) {
        return this.request('/stock/adjustment', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async getHistory(filters = {}) {
        const params = new URLSearchParams();
        if (filters.type) params.append('type', filters.type);
        if (filters.warehouseId) params.append('warehouseId', filters.warehouseId);
        if (filters.productId) params.append('productId', filters.productId);
        if (filters.status) params.append('status', filters.status);
        if (filters.category) params.append('category', filters.category);
        
        const queryString = params.toString();
        return this.request(`/stock/history${queryString ? '?' + queryString : ''}`);
    }

    async validateMovement(id) {
        return this.request(`/stock/validate/${id}`, {
            method: 'POST'
        });
    }

    async getInventory() {
        return this.request('/stock/inventory');
    }
}

// Export instance
const api = new ApiClient();
