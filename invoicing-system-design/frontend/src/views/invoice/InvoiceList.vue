<template>
  <div class="invoice-list">
    <el-card>
      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="发票号码">
            <el-input v-model="searchForm.invoiceNo" placeholder="请输入发票号码"></el-input>
          </el-form-item>
          <el-form-item label="客户名称">
            <el-input v-model="searchForm.customerName" placeholder="请输入客户名称"></el-input>
          </el-form-item>
          <el-form-item label="发票类型">
            <el-select v-model="searchForm.invoiceType" placeholder="请选择">
              <el-option label="增值税专用发票" value="SPECIAL"></el-option>
              <el-option label="增值税普通发票" value="NORMAL"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发票状态">
            <el-select v-model="searchForm.status" placeholder="请选择">
              <el-option label="草稿" value="DRAFT"></el-option>
              <el-option label="待审核" value="PENDING_REVIEW"></el-option>
              <el-option label="已开具" value="ISSUED"></el-option>
              <el-option label="已交付" value="DELIVERED"></el-option>
              <el-option label="已红冲" value="RED_CANCELLED"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="tableData" border>
        <el-table-column prop="invoiceNo" label="发票号码"></el-table-column>
        <el-table-column prop="invoiceCode" label="发票代码"></el-table-column>
        <el-table-column prop="invoiceType" label="发票类型">
          <template slot-scope="scope">
            {{ getInvoiceTypeName(scope.row.invoiceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="invoiceKind" label="发票种类">
          <template slot-scope="scope">
            <el-tag :type="scope.row.invoiceKind === 'BLUE' ? 'success' : 'danger'">
              {{ scope.row.invoiceKind === 'BLUE' ? '蓝字' : '红字' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户名称"></el-table-column>
        <el-table-column prop="totalAmount" label="总金额"></el-table-column>
        <el-table-column prop="taxAmount" label="税额"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="issueDate" label="开票日期">
          <template slot-scope="scope">
            {{ formatDate(scope.row.issueDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 'DRAFT'" type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 'PENDING_REVIEW'" type="text" @click="handleReview(scope.row)">审核</el-button>
            <el-button v-if="scope.row.status === 'PENDING_REVIEW'" type="text" @click="handleIssue(scope.row)">开具</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="currentPage" :page-sizes="[10, 20, 50]" :page-size="pageSize"
        :total="total" layout="total, sizes, prev, pager, next, jumper"></el-pagination>
    </el-card>
    <el-dialog title="发票详情" :visible.sync="dialogVisible" width="800px">
      <el-descriptions v-if="currentInvoice" :column="2" border>
        <el-descriptions-item label="发票号码">{{ currentInvoice.invoiceNo }}</el-descriptions-item>
        <el-descriptions-item label="发票代码">{{ currentInvoice.invoiceCode }}</el-descriptions-item>
        <el-descriptions-item label="发票类型">{{ getInvoiceTypeName(currentInvoice.invoiceType) }}</el-descriptions-item>
        <el-descriptions-item label="发票种类">{{ currentInvoice.invoiceKind === 'BLUE' ? '蓝字' : '红字' }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ currentInvoice.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客户税号">{{ currentInvoice.customerTaxId }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ currentInvoice.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="税额">{{ currentInvoice.taxAmount }}</el-descriptions-item>
        <el-descriptions-item label="价税合计">{{ currentInvoice.taxableAmount }}</el-descriptions-item>
        <el-descriptions-item label="开票日期">{{ formatDate(currentInvoice.issueDate) }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">{{ getStatusName(currentInvoice.status) }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentInvoiceDetails && currentInvoiceDetails.length > 0" style="margin-top: 20px;">
        <h4>发票明细</h4>
        <el-table :data="currentInvoiceDetails" border size="small">
          <el-table-column prop="feeType" label="费用类型"></el-table-column>
          <el-table-column prop="description" label="项目描述"></el-table-column>
          <el-table-column prop="totalAmount" label="金额"></el-table-column>
          <el-table-column prop="taxAmount" label="税额"></el-table-column>
          <el-table-column prop="taxRate" label="税率"></el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllInvoices, getInvoiceDetail } from '@/api/invoice'

export default {
  name: 'InvoiceList',
  data() {
    return {
      searchForm: {
        invoiceNo: '',
        customerName: '',
        invoiceType: '',
        status: ''
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      currentInvoice: null,
      currentInvoiceDetails: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      const params = {
        ...this.searchForm
      }
      getAllInvoices(params).then(res => {
        this.tableData = res.data
        this.total = this.tableData.length
      })
    },
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        invoiceNo: '',
        customerName: '',
        invoiceType: '',
        status: ''
      }
      this.currentPage = 1
      this.loadData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    handleView(row) {
      getInvoiceDetail(row.id).then(res => {
        this.currentInvoice = res.data.invoice
        this.currentInvoiceDetails = res.data.details
        this.dialogVisible = true
      })
    },
    handleEdit(row) {
      this.$message.info('编辑功能开发中')
    },
    handleReview(row) {
      this.$message.info('审核功能开发中')
    },
    handleIssue(row) {
      this.$message.info('开具功能开发中')
    },
    getInvoiceTypeName(type) {
      const types = {
        'SPECIAL': '增值税专用发票',
        'NORMAL': '增值税普通发票'
      }
      return types[type] || type
    },
    getStatusName(status) {
      const statuses = {
        'DRAFT': '草稿',
        'PENDING_REVIEW': '待审核',
        'ISSUED': '已开具',
        'DELIVERED': '已交付',
        'RED_CANCELLED': '已红冲'
      }
      return statuses[status] || status
    },
    getStatusType(status) {
      const types = {
        'DRAFT': 'info',
        'PENDING_REVIEW': 'warning',
        'ISSUED': 'success',
        'DELIVERED': 'primary',
        'RED_CANCELLED': 'danger'
      }
      return types[status] || 'info'
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString()
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
</style>