<template>
  <div class="batch-invoice">
    <el-card title="批量开票">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="客户名称">
          <el-select v-model="searchForm.customerId" placeholder="请选择客户">
            <el-option v-for="customer in customers" :key="customer.id" :label="customer.name" :value="customer.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开票状态">
          <el-select v-model="searchForm.status" placeholder="请选择">
            <el-option label="未开票" value="UNINVOICED"></el-option>
            <el-option label="部分开票" value="PARTIAL_INVOICED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border style="margin-top: 20px;" @selection-change="handleSelectionChange">
        <el-table-column type="selection"></el-table-column>
        <el-table-column prop="recordNo" label="还款记录号"></el-table-column>
        <el-table-column prop="customerName" label="客户名称"></el-table-column>
        <el-table-column prop="contractNo" label="合同编号"></el-table-column>
        <el-table-column prop="repaymentDate" label="还款日期">
          <template slot-scope="scope">{{ formatDate(scope.row.repaymentDate) }}</template>
        </el-table-column>
        <el-table-column prop="feeType" label="费用类型">
          <template slot-scope="scope">{{ getFeeTypeName(scope.row.feeType) }}</template>
        </el-table-column>
        <el-table-column prop="taxableAmount" label="可开票金额"></el-table-column>
        <el-table-column prop="invoicedAmount" label="已开票金额"></el-table-column>
        <el-table-column prop="remainingAmount" label="剩余金额"></el-table-column>
        <el-table-column prop="invoiceStatus" label="开票状态">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.invoiceStatus)">
              {{ getStatusName(scope.row.invoiceStatus) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="batch-actions">
        <span>已选择 {{ selectedItems.length }} 条记录</span>
        <el-button type="primary" @click="handleBatchInvoice" :disabled="selectedItems.length === 0">
          批量开票
        </el-button>
      </div>
      <el-divider></el-divider>
      <h4>批量任务列表</h4>
      <el-table :data="taskList" border>
        <el-table-column prop="taskName" label="任务名称"></el-table-column>
        <el-table-column prop="taskType" label="任务类型"></el-table-column>
        <el-table-column prop="totalCount" label="总数"></el-table-column>
        <el-table-column prop="successCount" label="成功"></el-table-column>
        <el-table-column prop="failedCount" label="失败"></el-table-column>
        <el-table-column prop="progress" label="进度">
          <template slot-scope="scope">
            <el-progress :percentage="scope.row.progress" :color="getProgressColor(scope.row.progress)"></el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="getTaskStatusType(scope.row.status)">
              {{ getTaskStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间">
          <template slot-scope="scope">{{ formatDate(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 'PENDING'" type="text" @click="handleStartTask(scope.row)">启动</el-button>
            <el-button v-if="scope.row.status === 'RUNNING'" type="text" @click="handlePauseTask(scope.row)">暂停</el-button>
            <el-button v-if="scope.row.status === 'PAUSED'" type="text" @click="handleResumeTask(scope.row)">继续</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getAllCustomers } from '@/api/customer'
import { getAllRecords } from '@/api/repayment'

export default {
  name: 'BatchInvoice',
  data() {
    return {
      searchForm: {
        customerId: '',
        status: ''
      },
      tableData: [],
      selectedItems: [],
      customers: [],
      taskList: [
        {
          id: 1,
          taskName: '2024年1月批量开票',
          taskType: 'INVOICE',
          totalCount: 100,
          successCount: 85,
          failedCount: 5,
          progress: 90,
          status: 'RUNNING',
          createdAt: '2024-01-15'
        },
        {
          id: 2,
          taskName: '2024年2月批量开票',
          taskType: 'INVOICE',
          totalCount: 80,
          successCount: 80,
          failedCount: 0,
          progress: 100,
          status: 'COMPLETED',
          createdAt: '2024-02-20'
        }
      ]
    }
  },
  mounted() {
    this.loadCustomers()
    this.loadRecords()
  },
  methods: {
    loadCustomers() {
      getAllCustomers().then(res => {
        this.customers = res.data
      })
    },
    loadRecords() {
      getAllRecords({ status: 'UNINVOICED' }).then(res => {
        this.tableData = res.data
      })
    },
    handleSearch() {
      getAllRecords(this.searchForm).then(res => {
        this.tableData = res.data
      })
    },
    handleSelectionChange(val) {
      this.selectedItems = val
    },
    handleBatchInvoice() {
      this.$message.success(`已创建批量开票任务，包含 ${this.selectedItems.length} 条记录`)
    },
    handleStartTask(row) {
      row.status = 'RUNNING'
      this.$message.success('任务已启动')
    },
    handlePauseTask(row) {
      row.status = 'PAUSED'
      this.$message.info('任务已暂停')
    },
    handleResumeTask(row) {
      row.status = 'RUNNING'
      this.$message.success('任务已继续')
    },
    getFeeTypeName(type) {
      const types = {
        'INTEREST': '利息',
        'FEE': '手续费',
        'PENALTY': '罚息',
        'COMMITMENT': '承诺费',
        'OTHER': '其他'
      }
      return types[type] || type
    },
    getStatusName(status) {
      const statuses = {
        'UNINVOICED': '未开票',
        'INVOICED': '已开票',
        'PARTIAL_INVOICED': '部分开票',
        'RED_CANCELLED': '已红冲'
      }
      return statuses[status] || status
    },
    getStatusType(status) {
      const types = {
        'UNINVOICED': 'danger',
        'INVOICED': 'success',
        'PARTIAL_INVOICED': 'warning',
        'RED_CANCELLED': 'info'
      }
      return types[status] || 'info'
    },
    getTaskStatusName(status) {
      const statuses = {
        'PENDING': '待启动',
        'RUNNING': '运行中',
        'PAUSED': '已暂停',
        'COMPLETED': '已完成',
        'FAILED': '失败'
      }
      return statuses[status] || status
    },
    getTaskStatusType(status) {
      const types = {
        'PENDING': 'info',
        'RUNNING': 'success',
        'PAUSED': 'warning',
        'COMPLETED': 'primary',
        'FAILED': 'danger'
      }
      return types[status] || 'info'
    },
    getProgressColor(progress) {
      if (progress === 100) return '#1989FA'
      if (progress >= 80) return '#67C23A'
      if (progress >= 50) return '#E6A23C'
      return '#F56C6C'
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString()
    }
  }
}
</script>

<style scoped>
.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 15px 0;
}
</style>