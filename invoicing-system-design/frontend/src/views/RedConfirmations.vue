<template>
  <div class="red-confirmations">
    <div class="page-header">
      <h1 class="ph-title">红冲确认</h1>
      <p class="ph-desc">管理红字发票确认单，支持确认和取消操作</p>
    </div>

    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label class="filter-label">确认单号</label>
          <el-input v-model="searchForm.confirmationNo" placeholder="请输入确认单号"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">原发票号码</label>
          <el-input v-model="searchForm.invoiceNo" placeholder="请输入发票号码"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">状态</label>
          <el-select v-model="searchForm.status" placeholder="全部状态" style="width: 100%">
            <el-option label="待确认" value="PENDING"></el-option>
            <el-option label="已确认" value="CONFIRMED"></el-option>
            <el-option label="已取消" value="CANCELLED"></el-option>
          </el-select>
        </div>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="handleSearch">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg> 查询
        </el-button>
        <el-button @click="handleReset">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg> 重置
        </el-button>
      </div>
    </div>

    <div class="card data-card">
      <div class="card-header">
        <h3 class="ch-title">红冲确认列表</h3>
        <div class="ch-right">
          <span class="ch-sub">共 {{ total }} 条记录</span>
        </div>
      </div>
      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>确认单号</th>
              <th>原发票号码</th>
              <th>原发票代码</th>
              <th>客户名称</th>
              <th>冲销原因</th>
              <th>冲销金额</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>确认时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in tableData" :key="item.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td class="cell-link">{{ item.confirmationNo }}</td>
              <td>{{ item.invoiceNo }}</td>
              <td>{{ item.invoiceCode }}</td>
              <td>{{ item.customerName }}</td>
              <td>
                <span class="status-tag" :class="getReasonClass(item.reason)">
                  {{ getReasonName(item.reason) }}
                </span>
              </td>
              <td class="col-amount">¥ {{ formatNumber(item.amount) }}</td>
              <td>
                <span class="status-tag" :class="getStatusClass(item.status)">
                  {{ getStatusName(item.status) }}
                </span>
              </td>
              <td>{{ formatDate(item.createdAt) }}</td>
              <td>{{ formatDate(item.confirmedAt) }}</td>
              <td>
                <div class="action-group">
                  <button v-if="item.status === 'PENDING'" class="action-btn confirm" title="确认红冲" @click="handleConfirm(item)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                  </button>
                  <button v-if="item.status === 'PENDING'" class="action-btn cancel" title="取消" @click="handleCancel(item)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" x2="9" y1="9" y2="15"/><line x1="9" x2="15" y1="9" y2="15"/></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="table-footer">
        <span class="tf-info">显示 {{ tableData.length }} 条，共 {{ total }} 条记录</span>
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
  </div>
</template>

<script>
import { getAllConfirmations, confirmRed, cancelRed } from '@/api/redConfirmation'

export default {
  name: 'RedConfirmations',
  data() {
    return {
      searchForm: {
        confirmationNo: '',
        invoiceNo: '',
        status: ''
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      getAllConfirmations({
        ...this.searchForm,
        page: this.currentPage,
        size: this.pageSize
      }).then(res => {
        this.tableData = res.data.records || res.data
        this.total = res.data.total || this.tableData.length
      })
    },
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { confirmationNo: '', invoiceNo: '', status: '' }
      this.currentPage = 1
      this.loadData()
    },
    handleConfirm(row) {
      this.$confirm('确定要确认红冲吗？此操作将生成红字发票并冲销原发票。', '提示', { type: 'warning' }).then(() => {
        confirmRed(row.id).then(() => {
          this.$message.success('红冲确认成功')
          this.loadData()
        })
      })
    },
    handleCancel(row) {
      this.$confirm('确定要取消该红冲申请吗？', '提示', { type: 'info' }).then(() => {
        cancelRed(row.id).then(() => {
          this.$message.success('已取消')
          this.loadData()
        })
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    getReasonName(reason) {
      const reasons = {
        'WRONG_INFO': '开票有误',
        'RETURN': '销货退回',
        'DISCOUNT': '销售折让',
        'OTHER': '其他'
      }
      return reasons[reason] || reason
    },
    getReasonClass(reason) {
      const classes = {
        'WRONG_INFO': 'warning',
        'RETURN': 'error',
        'DISCOUNT': 'info',
        'OTHER': 'neutral'
      }
      return classes[reason] || 'neutral'
    },
    getStatusName(status) {
      const statuses = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'CANCELLED': '已取消',
        'REJECTED': '已拒绝',
        'EXPIRED': '已超时'
      }
      return statuses[status] || status
    },
    getStatusClass(status) {
      const classes = {
        'PENDING': 'warning',
        'CONFIRMED': 'success',
        'CANCELLED': 'neutral',
        'REJECTED': 'error',
        'EXPIRED': 'neutral'
      }
      return classes[status] || 'neutral'
    },
    formatDate(date) {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatNumber(num) {
      if (num === null || num === undefined || num === '') return '0.00'
      return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    }
  }
}
</script>

<style scoped>
.red-confirmations {
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

.cell-link {
  color: var(--fin-primary-700);
  font-weight: 500;
  cursor: pointer;
}

.cell-link:hover {
  text-decoration: underline;
}

.col-amount {
  font-family: var(--fin-font-mono);
  font-weight: 600;
  text-align: right;
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

.status-tag.warning {
  background: #fef3c7;
  color: #d97706;
}

.status-tag.error {
  background: #fef2f2;
  color: #b91c1c;
}

.status-tag.info {
  background: #dbeafe;
  color: #2563eb;
}

.status-tag.neutral {
  background: #eef1f6;
  color: #475569;
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

.action-btn.confirm:hover {
  color: #059669;
}

.action-btn.cancel:hover {
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

@media (max-width: 768px) {
  .filter-row {
    grid-template-columns: 1fr;
  }
}
</style>