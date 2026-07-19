<template>
  <div class="repayments">
    <div class="page-header">
      <h1 class="ph-title">还款记录</h1>
      <p class="ph-desc">查看和管理客户还款明细，关联贷款合同</p>
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
          <label class="filter-label">还款日期</label>
          <div class="date-range-input">
            <el-date-picker v-model="filterForm.startDate" type="date" placeholder="开始日期" style="width: 100%"></el-date-picker>
            <span class="date-sep">至</span>
            <el-date-picker v-model="filterForm.endDate" type="date" placeholder="结束日期" style="width: 100%"></el-date-picker>
          </div>
        </div>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="searchRecords">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg> 查询
        </el-button>
        <el-button @click="addRecord">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg> 新增还款记录
        </el-button>
      </div>
    </div>

    <div class="card data-card">
      <div class="card-header">
        <h3 class="ch-title">还款记录列表</h3>
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
              <th>还款日期</th>
              <th>本金金额</th>
              <th>利息金额</th>
              <th>可开票金额</th>
              <th>已开票金额</th>
              <th>剩余可开票</th>
              <th>税率</th>
              <th>税额</th>
              <th>开票状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(record, index) in records" :key="record.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>{{ getContractNo(record.contractId) }}</td>
              <td>{{ getCustomerNameByContract(record.contractId) }}</td>
              <td>{{ record.repaymentDate ? record.repaymentDate.substring(0,10) : '' }}</td>
              <td class="text-right">¥ {{ formatNumber(record.principalAmount) }}</td>
              <td class="text-right">¥ {{ formatNumber(record.interestAmount) }}</td>
              <td class="text-right">¥ {{ formatNumber(record.taxableAmount) }}</td>
              <td class="text-right">¥ {{ formatNumber(record.invoicedAmount) }}</td>
              <td class="text-right">¥ {{ formatNumber(record.remainingAmount) }}</td>
              <td>{{ ((record.taxRate || 0) * 100).toFixed(1) }}%</td>
              <td class="text-right">¥ {{ formatNumber(record.taxAmount) }}</td>
              <td>
                <span class="status-tag" :class="getStatusClass(record.invoiceStatus)">
                  {{ getStatusText(record.invoiceStatus) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="table-footer">
        <span class="tf-info">显示 {{ records.length }} 条，共 {{ total }} 条记录</span>
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

    <el-dialog title="新增还款记录" :visible.sync="dialogVisible" width="600px">
      <el-form ref="recordForm" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="合同编号" prop="contractId">
          <el-select v-model="formData.contractId" placeholder="请选择合同" style="width: 100%">
            <el-option v-for="c in contracts" :key="c.id" :label="c.contractNo" :value="c.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="还款日期" prop="repaymentDate">
          <el-date-picker v-model="formData.repaymentDate" type="date" placeholder="请选择还款日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="本金金额" prop="principalAmount">
          <el-input v-model.number="formData.principalAmount" placeholder="请输入本金金额"></el-input>
        </el-form-item>
        <el-form-item label="利息金额" prop="interestAmount">
          <el-input v-model.number="formData.interestAmount" placeholder="请输入利息金额"></el-input>
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
  name: 'Repayments',
  data() {
    return {
      filterForm: {
        contractNo: '',
        customerId: '',
        startDate: '',
        endDate: ''
      },
      customers: [],
      contracts: [],
      records: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      formData: {
        id: null,
        contractId: '',
        repaymentDate: '',
        principalAmount: 0,
        interestAmount: 0
      },
      rules: {
        contractId: [{ required: true, message: '请选择合同', trigger: 'change' }],
        repaymentDate: [{ required: true, message: '请选择还款日期', trigger: 'change' }],
        interestAmount: [{ required: true, message: '请输入利息金额', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadCustomers()
    this.loadContracts()
    this.searchRecords()
  },
  methods: {
    loadCustomers() {
      this.$http.get('/customers').then(res => {
        this.customers = res.data
      })
    },
    loadContracts() {
      this.$http.get('/contracts').then(res => {
        this.contracts = res.data
      })
    },
    searchRecords() {
      const params = {}
      if (this.filterForm.customerId) params.customerId = this.filterForm.customerId
      if (this.filterForm.contractNo) params.contractNo = this.filterForm.contractNo
      this.$http.get('/repayments', { params }).then(res => {
        let list = res.data || []
        this.total = list.length
        const start = (this.currentPage - 1) * this.pageSize
        this.records = list.slice(start, start + this.pageSize)
      })
    },
    addRecord() {
      this.formData = {
        id: null,
        contractId: '',
        repaymentDate: '',
        principalAmount: 0,
        interestAmount: 0
      }
      this.dialogVisible = true
    },
    submitForm() {
      this.$refs.recordForm.validate((valid) => {
        if (valid) {
          this.$http.post('/repayments', this.formData).then(() => {
            this.$message.success('新增成功')
            this.dialogVisible = false
            this.searchRecords()
          })
        }
      })
    },
    formatNumber(num) {
      if (num === null || num === undefined || num === '') return '0.00'
      return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    getContractNo(contractId) {
      const c = this.contracts.find(c => c.id === contractId)
      return c ? c.contractNo : '-'
    },
    getCustomerNameByContract(contractId) {
      const c = this.contracts.find(c => c.id === contractId)
      if (!c) return '-'
      const cust = this.customers.find(cu => cu.id === c.customerId)
      return cust ? cust.customerName : '-'
    },
    getStatusText(status) {
      const map = {
        'UNINVOICED': '未开票',
        'PARTIAL_INVOICED': '部分开票',
        'INVOICED': '已开票',
        'RED_CANCELLED': '已红冲'
      }
      return map[status] || status
    },
    getStatusClass(status) {
      const map = {
        'UNINVOICED': 'warning',
        'PARTIAL_INVOICED': 'info',
        'INVOICED': 'success',
        'RED_CANCELLED': 'danger'
      }
      return map[status] || ''
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.searchRecords()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.searchRecords()
    }
  }
}
</script>

<style scoped>
.repayments {
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

.date-range-input {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-sep {
  font-size: 13px;
  color: var(--fin-muted-foreground);
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

.status-tag.warning {
  background: #fef3c7;
  color: #d97706;
}

.status-tag.info {
  background: #dbeafe;
  color: #2563eb;
}

.status-tag.danger {
  background: #fef2f2;
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