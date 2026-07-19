<template>
  <div class="blue-invoice">
    <div class="page-header">
      <h1 class="ph-title">开票申请</h1>
      <p class="ph-desc">根据还款记录申请开具电子发票</p>
    </div>

    <div class="step-bar">
      <div v-for="(step, index) in steps" :key="index" class="step-item" :class="{ active: currentStep >= index, done: currentStep > index }">
        <div class="step-icon">
          <svg v-if="currentStep > index" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <span class="step-label">{{ step.name }}</span>
        <span class="step-desc">{{ step.desc }}</span>
        <div v-if="index < steps.length - 1" class="step-line"></div>
      </div>
    </div>

    <div class="card filter-card">
      <div class="card-header">
        <h3 class="ch-title">筛选条件</h3>
        <button class="ch-action" @click="resetFilter">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>
          重置
        </button>
      </div>
      <div class="filter-grid">
        <div class="filter-item">
          <label class="filter-label">客户名称</label>
          <el-select v-model="filterForm.customerId" placeholder="请选择客户" style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.customerName" :value="c.id"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <label class="filter-label">合同编号</label>
          <el-input v-model="filterForm.contractNo" placeholder="请输入合同编号"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">还款日期</label>
          <div class="date-range-input">
            <el-date-picker v-model="filterForm.startDate" type="date" placeholder="开始日期" style="width: 100%"></el-date-picker>
            <span class="date-sep">至</span>
            <el-date-picker v-model="filterForm.endDate" type="date" placeholder="结束日期" style="width: 100%"></el-date-picker>
          </div>
        </div>
        <div class="filter-item">
          <label class="filter-label">开票状态</label>
          <el-select v-model="filterForm.invoiceStatus" placeholder="全部状态" style="width: 100%">
            <el-option label="未开票" value="UNINVOICED"></el-option>
            <el-option label="部分开票" value="PARTIAL_INVOICED"></el-option>
            <el-option label="已开票" value="INVOICED"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <label class="filter-label">税率</label>
          <el-select v-model="filterForm.taxRate" placeholder="全部税率" style="width: 100%">
            <el-option label="6%" :value="0.06"></el-option>
            <el-option label="5%" :value="0.05"></el-option>
            <el-option label="3%" :value="0.03"></el-option>
          </el-select>
        </div>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="searchRecords">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg> 查询
        </el-button>
        <el-button @click="exportRecords">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg> 导出
        </el-button>
      </div>
    </div>

    <div class="card data-card">
      <div class="card-header">
        <h3 class="ch-title">还款记录列表</h3>
        <div class="ch-right">
          <el-switch v-model="showOnlyUninvoiced" active-text="仅未开票" inactive-text="" @change="toggleUninvoicedFilter" class="uninvoiced-switch"></el-switch>
          <span class="ch-sub">已选择 {{ selectedRepayments.length }} 条记录</span>
        </div>
      </div>
      <div class="table-wrapper">
        <el-table ref="repaymentTable" :data="filteredRecords" @selection-change="handleSelectionChange" stripe border style="width: 100%" class="data-table">
          <el-table-column type="selection" width="45" align="center"></el-table-column>
          <el-table-column label="客户名称" min-width="120">
            <template slot-scope="scope">{{ getCustomerNameByContract(scope.row.contractId) }}</template>
          </el-table-column>
          <el-table-column label="合同编号" min-width="130">
            <template slot-scope="scope">{{ getContractNo(scope.row.contractId) }}</template>
          </el-table-column>
          <el-table-column label="还款日期" min-width="110">
            <template slot-scope="scope">{{ scope.row.repaymentDate ? scope.row.repaymentDate.substring(0,10) : '' }}</template>
          </el-table-column>
          <el-table-column label="利息金额" min-width="110" align="right">
            <template slot-scope="scope">¥ {{ formatNumber(scope.row.interestAmount) }}</template>
          </el-table-column>
          <el-table-column label="已开票金额" min-width="110" align="right">
            <template slot-scope="scope">¥ {{ formatNumber(scope.row.invoicedAmount) }}</template>
          </el-table-column>
          <el-table-column label="待开票金额" min-width="110" align="right">
            <template slot-scope="scope">¥ {{ formatNumber(scope.row.remainingAmount) }}</template>
          </el-table-column>
          <el-table-column label="税率" min-width="70" align="center">
            <template slot-scope="scope">{{ ((scope.row.taxRate || 0) * 100).toFixed(1) }}%</template>
          </el-table-column>
          <el-table-column label="税额" min-width="100" align="right">
            <template slot-scope="scope">¥ {{ formatNumber(scope.row.taxAmount) }}</template>
          </el-table-column>
          <el-table-column label="开票状态" min-width="90" align="center">
            <template slot-scope="scope">
              <span class="status-tag" :class="getStatusClass(scope.row.invoiceStatus)">{{ getStatusText(scope.row.invoiceStatus) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="table-footer">
        <span class="tf-info">共 {{ filteredRecords.length }} 条记录</span>
        <div class="pagination">
          <button class="pg-btn" :disabled="currentPage === 1">上一页</button>
          <span class="pg-current">{{ currentPage }} / {{ totalPages }}</span>
          <button class="pg-btn" :disabled="currentPage === totalPages">下一页</button>
        </div>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="bb-left">
        <div class="summary-item">
          <span class="si-label">已选记录</span>
          <span class="si-value">{{ selectedRepayments.length }} 条</span>
        </div>
        <div class="summary-item">
          <span class="si-label">待开票金额合计</span>
          <span class="si-value">¥ {{ formatNumber(calculateSelectedTotal('remainingAmount')) }}</span>
        </div>
        <div class="summary-item">
          <span class="si-label">税额合计</span>
          <span class="si-value">¥ {{ formatNumber(calculateSelectedTotal('taxAmount')) }}</span>
        </div>
        <div class="summary-item">
          <span class="si-label">价税合计</span>
          <span class="si-value highlight">¥ {{ formatNumber(calculateSelectedTotal('remainingAmount') + calculateSelectedTotal('taxAmount')) }}</span>
        </div>
      </div>
      <div class="bb-right">
        <el-button @click="previewInvoice">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg> 预览
        </el-button>
        <el-button type="primary" :disabled="selectedRepayments.length === 0" @click="submitInvoice">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg> 确认开票（已选 {{ selectedRepayments.length }} 条）
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getRepaymentRecords, createInvoice, batchCreateInvoice } from '@/api'

export default {
  name: 'BlueInvoice',
  data() {
    return {
      currentStep: 0,
      steps: [
        { name: '选择记录', desc: '筛选并勾选还款记录' },
        { name: '确认信息', desc: '核对开票信息' },
        { name: '提交申请', desc: '提交审核' }
      ],
      filterForm: {
        customerId: '',
        contractNo: '',
        startDate: '',
        endDate: '',
        invoiceStatus: '',
        taxRate: ''
      },
      customers: [],
      contracts: [],
      records: [],
      selectedIds: [],
      selectedRepayments: [],
      showOnlyUninvoiced: true,
      currentPage: 1,
      totalPages: 1
    }
  },
  computed: {
    filteredRecords() {
      if (this.showOnlyUninvoiced) {
        return this.records.filter(r => r.invoiceStatus === 'UNINVOICED' || r.invoiceStatus === 'PARTIAL_INVOICED')
      }
      return this.records
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
        this.contracts = res.data || []
      })
    },
    searchRecords() {
      const params = {}
      if (this.filterForm.customerId) params.customerId = this.filterForm.customerId
      if (this.filterForm.invoiceStatus) params.status = this.filterForm.invoiceStatus
      this.$http.get('/repayments', { params }).then(res => {
        this.records = res.data || []
        this.totalPages = Math.max(1, Math.ceil(this.records.length / 10))
        this.$nextTick(() => {
          if (this.$refs.repaymentTable) {
            this.$refs.repaymentTable.clearSelection()
          }
        })
        this.selectedRepayments = []
      })
    },
    resetFilter() {
      this.filterForm = {
        customerId: '',
        contractNo: '',
        startDate: '',
        endDate: '',
        invoiceStatus: '',
        taxRate: ''
      }
      this.searchRecords()
    },
    exportRecords() {
      this.$message.info('导出功能开发中')
    },
    handleSelectionChange(val) {
      this.selectedRepayments = val
    },
    calculateSelectedTotal(field) {
      return this.selectedRepayments.reduce((sum, r) => sum + (r[field] || 0), 0)
    },
    toggleUninvoicedFilter() {
      this.$nextTick(() => {
        if (this.$refs.repaymentTable) {
          this.$refs.repaymentTable.clearSelection()
        }
      })
      this.selectedRepayments = []
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
        'UNINVOICED': 'uninvoiced',
        'PARTIAL_INVOICED': 'partial',
        'INVOICED': 'invoiced',
        'RED_CANCELLED': 'red-invoiced'
      }
      return map[status] || ''
    },
    previewInvoice() {
      this.$message.info('预览功能开发中')
    },
    submitInvoice() {
      if (this.selectedRepayments.length === 0) return
      const count = this.selectedRepayments.length
      const total = this.calculateSelectedTotal('remainingAmount') + this.calculateSelectedTotal('taxAmount')
      this.$confirm(`确认将已选 ${count} 条还款记录合并开具一张发票？\n价税合计：¥ ${this.formatNumber(total)}`, '确认开票', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        batchCreateInvoice({
          recordIds: this.selectedRepayments.map(r => r.id),
          totalAmount: total,
          remainingAmount: this.calculateSelectedTotal('remainingAmount'),
          taxAmount: this.calculateSelectedTotal('taxAmount')
        }).then(() => {
          this.$message.success(`已成功提交 ${count} 条记录的开票申请`)
          this.selectedRepayments = []
          this.searchRecords()
        }).catch(err => {
          this.$message.error(err.message || '提交失败')
        })
      })
    }
  }
}
</script>

<style scoped>
.blue-invoice {
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

.step-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  margin-bottom: 20px;
  background: var(--fin-card);
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-lg);
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  padding: 0 40px;
}

.step-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--fin-muted);
  color: var(--fin-muted-foreground);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  transition: all 0.25s ease;
}

.step-item.active .step-icon {
  background: var(--fin-primary-700);
  color: #fff;
}

.step-item.done .step-icon {
  background: #059669;
  color: #fff;
}

.step-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--fin-muted-foreground);
  margin-bottom: 4px;
}

.step-item.active .step-label,
.step-item.done .step-label {
  color: var(--fin-foreground);
}

.step-desc {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.step-line {
  position: absolute;
  right: -40px;
  top: 18px;
  width: 80px;
  height: 2px;
  background: var(--fin-border);
}

.step-item.done .step-line {
  background: #059669;
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

.ch-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ch-sub {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.uninvoiced-switch {
  margin-right: 4px;
}

.ch-action {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border: none;
  background: transparent;
  border-radius: var(--fin-radius-sm);
  font-size: 12px;
  color: var(--fin-primary-700);
  cursor: pointer;
}

.ch-action:hover {
  background: var(--fin-primary-50);
}

.filter-grid {
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
}

.data-table >>> .el-table__header th {
  background: var(--fin-muted);
  font-size: 12.5px;
  font-weight: 600;
  color: var(--fin-foreground);
}

.data-table >>> .el-table__body td {
  font-size: 13px;
  color: var(--fin-foreground);
}

.data-table >>> .el-table__row:hover > td {
  background: var(--fin-muted);
}

.data-table >>> .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: var(--fin-primary-700);
  border-color: var(--fin-primary-700);
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

.status-tag.uninvoiced {
  background: #fef3c7;
  color: #d97706;
}

.status-tag.partial {
  background: #dbeafe;
  color: #2563eb;
}

.status-tag.invoiced {
  background: #ecfdf5;
  color: #059669;
}

.status-tag.red-invoiced {
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

.pagination {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pg-btn {
  padding: 4px 12px;
  border: 1px solid var(--fin-border);
  background: var(--fin-card);
  border-radius: var(--fin-radius-sm);
  font-size: 12.5px;
  color: var(--fin-foreground);
  cursor: pointer;
}

.pg-btn:hover:not(:disabled) {
  background: var(--fin-muted);
}

.pg-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pg-current {
  font-size: 12.5px;
  color: var(--fin-muted-foreground);
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 220px;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: var(--fin-card);
  border-top: 1px solid var(--fin-border);
  box-shadow: 0 -2px 8px rgba(15, 31, 66, 0.06);
}

.bb-left {
  display: flex;
  gap: 32px;
}

.summary-item {
  display: flex;
  flex-direction: column;
}

.si-label {
  font-size: 12px;
  color: var(--fin-muted-foreground);
  margin-bottom: 2px;
}

.si-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--fin-foreground);
}

.si-value.highlight {
  color: var(--fin-primary-700);
  font-size: 18px;
}

.bb-right {
  display: flex;
  gap: 10px;
}

@media (max-width: 1200px) {
  .filter-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }
  .step-bar {
    flex-direction: column;
    gap: 16px;
  }
  .step-line {
    display: none;
  }
  .bottom-bar {
    position: static;
    margin-top: 20px;
  }
  .bb-left {
    flex-direction: column;
    gap: 8px;
  }
}
</style>