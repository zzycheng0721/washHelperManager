import request from './request';

const extractData = (payload) => payload?.data ?? payload ?? {};

const normalizeList = (payload) => {
  const body = extractData(payload);

  if (Array.isArray(body?.items)) {
    return body;
  }

  if (Array.isArray(body)) {
    return {
      items: body,
      page: 1,
      pageSize: body.length,
      total: body.length
    };
  }

  if (Array.isArray(body?.data)) {
    return {
      items: body.data,
      page: 1,
      pageSize: body.data.length,
      total: body.data.length
    };
  }

  return {
    items: [],
    page: 1,
    pageSize: 20,
    total: 0
  };
};

export const ordersApi = {
  list(params = {}) {
    return request.get('/orders', { params }).then(normalizeList);
  },
  create(payload) {
    return request.post('/orders', payload).then(extractData);
  },
  updateStatus(id, status) {
    return request.put(`/orders/${id}/status`, { status }).then(extractData);
  },
  remove(id) {
    return request.delete(`/orders/${id}`).then(extractData);
  },
  print(id) {
    return request.post(`/orders/${id}/print`).then(extractData);
  }
};

export const customersApi = {
  list(params = {}) {
    return request.get('/customers', { params }).then(normalizeList);
  },
  create(payload) {
    return request.post('/customers', payload).then(extractData);
  },
  get(id) {
    return request.get(`/customers/${id}`).then(extractData);
  },
  update(id, payload) {
    return request.put(`/customers/${id}`, payload).then(extractData);
  },
  patch(id, payload) {
    return request.patch(`/customers/${id}`, payload).then(extractData);
  },
  remove(id) {
    return request.delete(`/customers/${id}`).then(extractData);
  },
  contact(id) {
    return request.post(`/customers/${id}/contact`).then(extractData);
  },
  updateBalance(id, balance) {
    return request.patch(`/customers/${id}/balance`, { balance }).then(extractData);
  }
};

export const inventoryApi = {
  list(params = {}) {
    return request.get('/inventory', { params }).then(normalizeList);
  },
  create(payload) {
    return request.post('/inventory', payload).then(extractData);
  },
  update(id, payload) {
    return request.put(`/inventory/${id}`, payload).then(extractData);
  }
};

export const employeesApi = {
  list(params = {}) {
    return request.get('/employees', { params }).then(normalizeList);
  },
  get(id) {
    return request.get(`/employees/${id}`).then(extractData);
  },
  create(payload) {
    return request.post('/employees', payload).then(extractData);
  },
  update(id, payload) {
    return request.put(`/employees/${id}`, payload).then(extractData);
  },
  patch(id, payload) {
    return request.patch(`/employees/${id}`, payload).then(extractData);
  },
  remove(id) {
    return request.delete(`/employees/${id}`).then(extractData);
  },
  resetPassword(id, password) {
    return request.patch(`/employees/${id}/password`, { password }).then(extractData);
  }
};

export const serviceItemsApi = {
  list(params = {}) {
    return request.get('/services', { params }).then(normalizeList);
  },
  get(id) {
    return request.get(`/services/${id}`).then(extractData);
  },
  create(payload) {
    return request.post('/services', payload).then(extractData);
  },
  update(id, payload) {
    return request.put(`/services/${id}`, payload).then(extractData);
  },
  patch(id, payload) {
    return request.patch(`/services/${id}`, payload).then(extractData);
  },
  remove(id) {
    return request.delete(`/services/${id}`).then(extractData);
  }
};

export const shopApi = {
  getInfo() {
    return request.get('/shop/info').then(extractData);
  },
  updateInfo(payload) {
    return request.put('/shop/info', payload).then(extractData);
  },
  getHours() {
    return request.get('/shop/operating-hours').then(extractData);
  },
  updateHours(payload) {
    return request.put('/shop/operating-hours', payload).then(extractData);
  }
};

export const walletApi = {
  getRechargePackages() {
    return request.get('/recharge/packages').then(extractData);
  },
  recharge(customerId, payload) {
    return request.post(`/customers/${customerId}/wallet/recharge`, payload).then(extractData);
  },
  rechargeWithoutCustomer(payload) {
    return request.post('/wallet/recharge', payload).then(extractData);
  },
  createTransaction(customerId, payload) {
    return request.post(`/customers/${customerId}/wallet/transactions`, payload).then(extractData);
  },
  createGlobalTransaction(payload) {
    return request.post('/transactions', payload).then(extractData);
  },
  listTransactions(params = {}) {
    return request.get('/transactions', { params }).then(normalizeList);
  },
  listCustomerTransactions(customerId, params = {}) {
    return request.get(`/customers/${customerId}/wallet/transactions`, { params }).then(normalizeList);
  }
};

export const receiptTemplatesApi = {
  get() {
    return request.get('/receipt-template').then(extractData);
  },
  update(payload) {
    return request.put('/receipt-template', payload).then(extractData);
  }
};

export const aiApi = {
  chat() {
    return request.get('/ai').then(extractData);
  }
};

export { extractData, normalizeList };
