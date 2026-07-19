<template>
  <div class="customers">
    <div class="page-header">
      <h1 class="ph-title">客户管理</h1>
      <p class="ph-desc">管理开票客户信息，支持新增、编辑和删除</p>
    </div>

    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label class="filter-label">客户名称</label>
          <el-input v-model="filterForm.customerName" placeholder="请输入客户名称"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">税号</label>
          <el-input v-model="filterForm.taxNo" placeholder="请输入税号"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">客户状态</label>
          <el-select v-model="filterForm.status" placeholder="全部状态" style="width: 100%">
            <el-option label="正常" value="ACTIVE"></el-option>
            <el-option label="停用" value="INACTIVE"></el-option>
          </el-select>
        </div>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="searchCustomers">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg> 查询
        </el-button>
        <el-button @click="addCustomer">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg> 新增客户
        </el-button>
      </div>
    </div>

    <div class="card data-card">
      <div class="card-header">
        <h3 class="ch-title">客户列表</h3>
        <div class="ch-right">
          <span class="ch-sub">共 {{ total }} 条记录</span>
        </div>
      </div>
      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>客户名称</th>
              <th>税号</th>
              <th>联系人</th>
              <th>联系电话</th>
              <th>开户行</th>
              <th>银行账号</th>
              <th>地址</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(customer, index) in customers" :key="customer.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>{{ customer.customerName }}</td>
              <td>{{ customer.taxNo }}</td>
              <td>{{ customer.contactPerson }}</td>
              <td>{{ customer.contactPhone }}</td>
              <td>{{ customer.bankName }}</td>
              <td>{{ customer.bankAccount }}</td>
              <td class="ellipsis">{{ customer.address }}</td>
              <td>
                <span class="status-tag" :class="customer.status === 'ACTIVE' ? 'success' : 'danger'">
                  {{ customer.status === 'ACTIVE' ? '正常' : '停用' }}
                </span>
              </td>
              <td>
                <div class="action-group">
                  <button class="action-btn edit" title="编辑" @click="editCustomer(customer)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button class="action-btn delete" title="删除" @click="deleteCustomer(customer)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="table-footer">
        <span class="tf-info">显示 {{ customers.length }} 条，共 {{ total }} 条记录</span>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="新增客户" :visible.sync="dialogVisible" width="600px">
      <el-form ref="customerForm" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="formData.customerName" placeholder="请输入客户名称"></el-input>
        </el-form-item>
        <el-form-item label="税号" prop="taxNo">
          <el-input v-model="formData.taxNo" placeholder="请输入税号"></el-input>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="开户行">
          <el-input v-model="formData.bankName" placeholder="请输入开户行"></el-input>
        </el-form-item>
        <el-form-item label="银行账号">
          <el-input v-model="formData.bankAccount" placeholder="请输入银行账号"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="formData.address" type="textarea" :rows="2" placeholder="请输入地址"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Customers',
  data() {
    return {
      filterForm: {
        customerName: '',
        taxNo: '',
        status: ''
      },
      customers: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      isEdit: false,
      formData: {
        id: null,
        customerName: '',
        taxNo: '',
        contactPerson: '',
        contactPhone: '',
        bankName: '',
        bankAccount: '',
        address: ''
      },
      rules: {
        customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
        taxNo: [{ required: true, message: '请输入税号', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.searchCustomers()
  },
  methods: {
    searchCustomers() {
      this.$http.post('/api/customers/search', {
        ...this.filterForm,
        page: this.currentPage,
        size: this.pageSize
      }).then(res => {
        this.customers = res.data.records
        this.total = res.data.total
      })
    },
    addCustomer() {
      this.isEdit = false
      this.formData = {
        id: null,
        customerName: '',
        taxNo: '',
        contactPerson: '',
        contactPhone: '',
        bankName: '',
        bankAccount: '',
        address: ''
      }
      this.dialogVisible = true
    },
    editCustomer(customer) {
      this.isEdit = true
      this.formData = { ...customer }
      this.dialogVisible = true
    },
    deleteCustomer(customer) {
      this.$confirm(`确定删除客户「${customer.customerName}」吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/api/customers/${customer.id}`).then(() => {
          this.$message.success('删除成功')
          this.searchCustomers()
        }).catch(err => {
          this.$message.error(err.message || '删除失败')
        })
      })
    },
    submitForm() {
      this.$refs.customerForm.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            this.$http.put('/api/customers', this.formData).then(() => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.searchCustomers()
            })
          } else {
            this.$http.post('/api/customers', this.formData).then(() => {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.searchCustomers()
            })
          }
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.searchCustomers()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.searchCustomers()
    }
  }
}
</script>

<style scoped>
.customers {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.ph-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin: 0 0 4px;
}

.ph-desc {
  font-size: 13px;
  color: var(--fin-muted-foreground);
  margin: 0;
}

.card {
  background: var(--fin-card);
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-lg);
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--fin-border);
}

.ch-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin: 0;
}

.ch-sub {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding: 20px;
}

.filter-item {
  display: flex;
  flex-direction: column;
}

.filter-label {
  font-size: 12.5px;
  font-weight: 500;
  color: var(--fin-foreground);
  margin-bottom: 6px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  padding: 0 20px 20px;
}

.data-card {
  overflow: hidden;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: var(--fin-muted);
  padding: 12px 16px;
  text-align: left;
  font-size: 12.5px;
  font-weight: 600;
  color: var(--fin-foreground);
  white-space: nowrap;
}

.data-table td {
  padding: 12px 16px;
  border-bottom: 1px solid var(--fin-border);
  font-size: 13px;
  color: var(--fin-foreground);
}

.data-table tbody tr:hover {
  background: var(--fin-muted);
}

.ellipsis {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag {
  padding: 2px 8px;
  border-radius: var(--fin-radius-sm);
  font-size: 11.5px;
  font-weight: 500;
}

.status-tag.success {
  background: #ecfdf5;
  color: #059669;
}

.status-tag.danger {
  background: #fef2f2;
  color: #b91c1c;
}

.action-group {
  display: flex;
  gap: 6px;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: var(--fin-radius-sm);
  color: var(--fin-muted-foreground);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background: var(--fin-muted);
}

.action-btn.edit:hover {
  color: var(--fin-primary-700);
}

.action-btn.delete:hover {
  color: #b91c1c;
}

.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-top: 1px solid var(--fin-border);
}

.tf-info {
  font-size: 12.5px;
  color: var(--fin-muted-foreground);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .filter-row {
    grid-template-columns: 1fr;
  }
}
</style>