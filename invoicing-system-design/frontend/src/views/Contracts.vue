<template>
  <div class="contracts">
    <div class="page-header">
      <h1 class="ph-title">贷款合同</h1>
      <p class="ph-desc">管理贷款合同信息，关联客户和还款记录</p>
    </div>

    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label class="filter-label">合同编号</label>
          <el-input v-model="filterForm.contractNo" placeholder="请输入合同编号"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">客户名称</label>
          <el-select v-model="filterForm.customerId" placeholder="请选择客户" style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.customerName" :value="c.id"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <label class="filter-label">合同状态</label>
          <el-select v-model="filterForm.status" placeholder="全部状态" style="width: 100%">
            <el-option label="正常" value="ACTIVE"></el-option>
            <el-option label="已结清" value="SETTLED"></el-option>
            <el-option label="已终止" value="TERMINATED"></el-option>
          </el-select>
        </div>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="searchContracts">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg> 查询
        </el-button>
        <el-button @click="addContract">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg> 新增合同
        </el-button>
      </div>
    </div>

    <div class="card data-card">
      <div class="card-header">
        <h3 class="ch-title">合同列表</h3>
        <div class="ch-right">
          <span class="ch-sub">共 {{ total }} 条记录</span>
        </div>
      </div>
      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>合同编号</th>
              <th>客户名称</th>
              <th>贷款金额</th>
              <th>贷款利率</th>
              <th>贷款期限</th>
              <th>起息日期</th>
              <th>到期日期</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(contract, index) in contracts" :key="contract.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>{{ contract.contractNo }}</td>
              <td>{{ contract.customerName }}</td>
              <td class="text-right">¥ {{ formatNumber(contract.loanAmount) }}</td>
              <td>{{ (contract.interestRate * 100).toFixed(2) }}%</td>
              <td>{{ contract.loanTerm }} 个月</td>
              <td>{{ contract.startDate }}</td>
              <td>{{ contract.endDate }}</td>
              <td>
                <span class="status-tag" :class="getStatusClass(contract.status)">
                  {{ getStatusText(contract.status) }}
                </span>
              </td>
              <td>
                <div class="action-group">
                  <button class="action-btn edit" title="编辑" @click="editContract(contract)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button class="action-btn delete" title="删除" @click="deleteContract(contract)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="table-footer">
        <span class="tf-info">显示 {{ contracts.length }} 条，共 {{ total }} 条记录</span>
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

    <el-dialog title="新增合同" :visible.sync="dialogVisible" width="600px">
      <el-form ref="contractForm" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="合同编号" prop="contractNo">
          <el-input v-model="formData.contractNo" placeholder="请输入合同编号"></el-input>
        </el-form-item>
        <el-form-item label="客户名称" prop="customerId">
          <el-select v-model="formData.customerId" placeholder="请选择客户" style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.customerName" :value="c.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="贷款金额" prop="loanAmount">
          <el-input v-model.number="formData.loanAmount" placeholder="请输入贷款金额"></el-input>
        </el-form-item>
        <el-form-item label="贷款利率">
          <el-input v-model.number="formData.interestRate" placeholder="请输入贷款利率（如 0.06）"></el-input>
        </el-form-item>
        <el-form-item label="贷款期限">
          <el-input v-model.number="formData.loanTerm" placeholder="请输入贷款期限（月）"></el-input>
        </el-form-item>
        <el-form-item label="起息日期">
          <el-date-picker v-model="formData.startDate" type="date" placeholder="请选择起息日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="到期日期">
          <el-date-picker v-model="formData.endDate" type="date" placeholder="请选择到期日期" style="width: 100%"></el-date-picker>
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
  name: 'Contracts',
  data() {
    return {
      filterForm: {
        contractNo: '',
        customerId: '',
        status: ''
      },
      customers: [],
      contracts: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      isEdit: false,
      formData: {
        id: null,
        contractNo: '',
        customerId: '',
        loanAmount: 0,
        interestRate: 0.06,
        loanTerm: 12,
        startDate: '',
        endDate: ''
      },
      rules: {
        contractNo: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
        customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
        loanAmount: [{ required: true, message: '请输入贷款金额', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadCustomers()
    this.searchContracts()
  },
  methods: {
    loadCustomers() {
      this.$http.get('/api/customers').then(res => {
        this.customers = res.data
      })
    },
    searchContracts() {
      this.$http.post('/api/loan-contracts/search', {
        ...this.filterForm,
        page: this.currentPage,
        size: this.pageSize
      }).then(res => {
        this.contracts = res.data.records
        this.total = res.data.total
      })
    },
    addContract() {
      this.isEdit = false
      this.formData = {
        id: null,
        contractNo: '',
        customerId: '',
        loanAmount: 0,
        interestRate: 0.06,
        loanTerm: 12,
        startDate: '',
        endDate: ''
      }
      this.dialogVisible = true
    },
    editContract(contract) {
      this.isEdit = true
      this.formData = { ...contract }
      this.dialogVisible = true
    },
    deleteContract(contract) {
      this.$confirm(`确定删除合同「${contract.contractNo}」吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/api/loan-contracts/${contract.id}`).then(() => {
          this.$message.success('删除成功')
          this.searchContracts()
        }).catch(err => {
          this.$message.error(err.message || '删除失败')
        })
      })
    },
    submitForm() {
      this.$refs.contractForm.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            this.$http.put('/api/loan-contracts', this.formData).then(() => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.searchContracts()
            })
          } else {
            this.$http.post('/api/loan-contracts', this.formData).then(() => {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.searchContracts()
            })
          }
        }
      })
    },
    formatNumber(num) {
      return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    getStatusText(status) {
      const map = {
        'ACTIVE': '正常',
        'SETTLED': '已结清',
        'TERMINATED': '已终止'
      }
      return map[status] || status
    },
    getStatusClass(status) {
      const map = {
        'ACTIVE': 'success',
        'SETTLED': 'info',
        'TERMINATED': 'danger'
      }
      return map[status] || ''
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.searchContracts()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.searchContracts()
    }
  }
}
</script>

<style scoped>
.contracts {
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

.text-right {
  text-align: right;
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

.status-tag.info {
  background: #dbeafe;
  color: #2563eb;
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