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

            // Handle empty response for DELETE requests
            if (response.status === 204 || options.method === 'DELETE') {
                return { success: true };
            }

            const text = await response.text();
            return text ? JSON.parse(text) : { success: true };
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

    async updateProduct(id, product) {
        return this.request(`/products/${id}`, {
            method: 'PUT',
            body: JSON.stringify(product)
        });
    }

    async deleteProduct(id) {
        return this.request(`/products/${id}`, {
            method: 'DELETE'
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

    async updateWarehouse(id, warehouse) {
        return this.request(`/warehouses/${id}`, {
            method: 'PUT',
            body: JSON.stringify(warehouse)
        });
    }

    async deleteWarehouse(id) {
        return this.request(`/warehouses/${id}`, {
            method: 'DELETE'
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

    async updateSupplier(id, supplier) {
        return this.request(`/suppliers/${id}`, {
            method: 'PUT',
            body: JSON.stringify(supplier)
        });
    }

    async deleteSupplier(id) {
        return this.request(`/suppliers/${id}`, {
            method: 'DELETE'
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

    async updateMovementStatus(id, status) {
        return this.request(`/stock/movement/${id}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
    }

    async deleteMovement(id) {
        return this.request(`/stock/movement/${id}`, {
            method: 'DELETE'
        });
    }

    async getInventory() {
        return this.request('/stock/inventory');
    }

    async getStockByLocation(productId, warehouseId) {
        const params = new URLSearchParams();
        if (productId) params.append('productId', productId);
        if (warehouseId) params.append('warehouseId', warehouseId);
        return this.request(`/stock/inventory?${params.toString()}`);
    }

    // User Profile
    async getUserProfile() {
        return this.request('/users/profile');
    }

    async updateProfile(data) {
        return this.request('/users/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async changePassword(data) {
        return this.request('/users/change-password', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }
}

// Export instance
const api = new ApiClient();
