<template>
  <div class="invoice-list">
    <div class="page-header">
      <h1 class="ph-title">开票记录</h1>
      <p class="ph-desc">查看和管理所有开票记录，支持红冲操作</p>
    </div>

    <div class="summary-bar">
      <div class="summary-card">
        <div class="sc-icon blue">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
        </div>
        <div class="sc-info">
          <div class="sc-value">¥ {{ formatNumber(summary.totalAmount) }}</div>
          <div class="sc-label">开票总额</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="sc-icon green">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        </div>
        <div class="sc-info">
          <div class="sc-value">{{ summary.invoicedCount }}</div>
          <div class="sc-label">已开票数量</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="sc-icon red">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
        </div>
        <div class="sc-info">
          <div class="sc-value">¥ {{ formatNumber(summary.redAmount) }}</div>
          <div class="sc-label">红字冲销金额</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="sc-icon orange">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
        </div>
        <div class="sc-info">
          <div class="sc-value">{{ summary.pendingCount }}</div>
          <div class="sc-label">处理中数量</div>
        </div>
      </div>
    </div>

    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label class="filter-label">发票编号</label>
          <el-input v-model="filterForm.invoiceNo" placeholder="请输入发票编号"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">客户名称</label>
          <el-select v-model="filterForm.customerId" placeholder="请选择客户" style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.customerName" :value="c.id"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <label class="filter-label">开票日期</label>
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          ></el-date-picker>
        </div>
        <div class="filter-item">
          <label class="filter-label">发票状态</label>
          <el-select v-model="filterForm.status" placeholder="全部状态" style="width: 100%">
            <el-option label="草稿" value="DRAFT"></el-option>
            <el-option label="已开票" value="INVOICED"></el-option>
            <el-option label="部分红冲" value="PARTIAL_RED"></el-option>
            <el-option label="已红冲" value="RED_INVOICED"></el-option>
            <el-option label="处理中" value="PROCESSING"></el-option>
            <el-option label="开票失败" value="FAILED"></el-option>
          </el-select>
        </div>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="searchInvoices">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg> 查询
        </el-button>
        <el-button @click="resetFilter">重置</el-button>
        <el-button @click="exportInvoices">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg> 导出
        </el-button>
      </div>
    </div>

    <div class="card data-card">
      <div class="card-header">
        <h3 class="ch-title">发票列表</h3>
        <div class="ch-right">
          <span class="ch-sub">共 {{ total }} 条记录</span>
        </div>
      </div>
      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>发票编号</th>
              <th>客户名称</th>
              <th>合同编号</th>
              <th>开票日期</th>
              <th>发票类型</th>
              <th>价税合计</th>
              <th>不含税金额</th>
              <th>税额</th>
              <th>税率</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(invoice, index) in invoices" :key="invoice.id">
              <td><span class="link-text" @click="showInvoiceDetail(invoice)">{{ invoice.invoiceNo }}</span></td>
              <td>{{ invoice.customerName }}</td>
              <td>{{ invoice.contractNo }}</td>
              <td>{{ invoice.invoiceDate }}</td>
              <td>
                <span class="type-tag" :class="invoice.invoiceType === 'BLUE' ? 'blue' : 'red'">
                  {{ invoice.invoiceType === 'BLUE' ? '蓝字发票' : '红字发票' }}
                </span>
              </td>
              <td class="text-right">¥ {{ formatNumber(invoice.totalAmount) }}</td>
              <td class="text-right">¥ {{ formatNumber(invoice.amount) }}</td>
              <td class="text-right">¥ {{ formatNumber(invoice.taxAmount) }}</td>
              <td>{{ (invoice.taxRate * 100).toFixed(1) }}%</td>
              <td>
                <span class="status-tag" :class="getStatusClass(invoice.status)">{{ getStatusText(invoice.status) }}</span>
              </td>
              <td>
                <div class="action-group">
                  <button class="action-btn view" title="查看详情" @click="showInvoiceDetail(invoice)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
                  </button>
                  <button class="action-btn download" title="下载发票" @click="downloadInvoice(invoice)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg>
                  </button>
                  <button v-if="invoice.status === 'INVOICED'" class="action-btn deliver" title="交付" @click="deliverInvoiceAction(invoice)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 2L11 13"/><path d="M22 2L15 22L11 13L2 9L22 2Z"/></svg>
                  </button>
                  <button v-if="invoice.status === 'INVOICED'" class="action-btn red-chong" title="红字冲销" @click="redInvoice(invoice)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
                  </button>
                  <button v-if="invoice.status === 'DRAFT'" class="action-btn delete" title="删除" @click="deleteInvoiceAction(invoice)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="table-footer">
        <span class="tf-info">显示 {{ invoices.length }} 条，共 {{ total }} 条记录</span>
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

    <!-- 红字冲销对话框 -->
    <el-dialog title="红字冲销" :visible.sync="redDialogVisible" width="720px" :close-on-click-modal="false">
      <div class="red-form">
        <div class="rf-item">
          <label class="rf-label">原发票编号</label>
          <span class="rf-value">{{ currentInvoice?.invoiceNo }}</span>
        </div>
        <div class="rf-item">
          <label class="rf-label">原发票金额</label>
          <span class="rf-value">¥ {{ formatNumber(currentInvoice?.totalAmount || 0) }}</span>
        </div>
        <div class="rf-item">
          <label class="rf-label"><span class="rf-required">*</span> 红冲原因</label>
          <el-select v-model="redForm.reason" placeholder="请选择红冲原因" style="width: 100%">
            <el-option label="开票有误" value="INVOICE_ERROR"></el-option>
            <el-option label="销货退回" value="SALES_RETURN"></el-option>
            <el-option label="销售折让" value="SALES_DISCOUNT"></el-option>
            <el-option label="其他" value="OTHER"></el-option>
          </el-select>
        </div>
        <div class="rf-item">
          <label class="rf-label"><span class="rf-required">*</span> 红冲金额</label>
          <div class="rf-amount-row">
            <el-radio-group v-model="redForm.amountType" @change="handleAmountTypeChange">
              <el-radio label="full">全额红冲</el-radio>
              <el-radio label="partial">部分红冲</el-radio>
            </el-radio-group>
          </div>
          <el-input
            v-if="redForm.amountType === 'partial'"
            v-model="redForm.redAmount"
            type="number"
            placeholder="请输入红冲金额"
            style="margin-top: 8px"
          >
            <template slot="prepend">¥</template>
          </el-input>
          <div v-if="redForm.amountType === 'full'" class="rf-amount-hint">
            全额红冲金额：¥ {{ formatNumber(currentInvoice?.totalAmount || 0) }}
          </div>
        </div>
        <div v-if="repayments.length > 1" class="rf-item">
          <label class="rf-label">选择被红冲的还款记录</label>
          <div class="rf-repayment-tip">该发票关联多条还款记录，请选择需要红冲的还款记录</div>
          <table class="rf-repayment-table">
            <thead>
              <tr>
                <th style="width: 40px"></th>
                <th>还款编号</th>
                <th>还款日期</th>
                <th>还款金额</th>
                <th>已开票金额</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="rp in repayments" :key="rp.id">
                <td><el-checkbox v-model="rp.checked" @change="handleRepaymentCheck"></el-checkbox></td>
                <td>{{ rp.repaymentNo }}</td>
                <td>{{ rp.repaymentDate }}</td>
                <td class="text-right">¥ {{ formatNumber(rp.amount) }}</td>
                <td class="text-right">¥ {{ formatNumber(rp.invoicedAmount) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="rf-item">
          <label class="rf-label">冲销说明</label>
          <el-input v-model="redForm.description" type="textarea" :rows="3" placeholder="请输入冲销说明"></el-input>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="redDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitRedInvoice">确认冲销</el-button>
      </div>
    </el-dialog>

    <!-- 发票详情对话框 -->
    <el-dialog title="发票详情" :visible.sync="detailDialogVisible" width="680px" :close-on-click-modal="false">
      <div v-if="invoiceDetail" class="detail-content">
        <div class="detail-section">
          <h4 class="ds-title">基本信息</h4>
          <div class="ds-grid">
            <div class="ds-item">
              <span class="ds-label">发票编号</span>
              <span class="ds-value">{{ invoiceDetail.invoiceNo }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">发票类型</span>
              <span class="ds-value">
                <span class="type-tag" :class="invoiceDetail.invoiceType === 'BLUE' ? 'blue' : 'red'">
                  {{ invoiceDetail.invoiceType === 'BLUE' ? '蓝字发票' : '红字发票' }}
                </span>
              </span>
            </div>
            <div class="ds-item">
              <span class="ds-label">客户名称</span>
              <span class="ds-value">{{ invoiceDetail.customerName }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">合同编号</span>
              <span class="ds-value">{{ invoiceDetail.contractNo }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">开票日期</span>
              <span class="ds-value">{{ invoiceDetail.invoiceDate }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">状态</span>
              <span class="ds-value">
                <span class="status-tag" :class="getStatusClass(invoiceDetail.status)">{{ getStatusText(invoiceDetail.status) }}</span>
              </span>
            </div>
          </div>
        </div>
        <div class="detail-section">
          <h4 class="ds-title">金额信息</h4>
          <div class="ds-grid">
            <div class="ds-item">
              <span class="ds-label">价税合计</span>
              <span class="ds-value amount">¥ {{ formatNumber(invoiceDetail.totalAmount) }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">不含税金额</span>
              <span class="ds-value amount">¥ {{ formatNumber(invoiceDetail.amount) }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">税额</span>
              <span class="ds-value amount">¥ {{ formatNumber(invoiceDetail.taxAmount) }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">税率</span>
              <span class="ds-value">{{ (invoiceDetail.taxRate * 100).toFixed(1) }}%</span>
            </div>
          </div>
        </div>
        <div v-if="invoiceDetail.kingdeeInfo" class="detail-section">
          <h4 class="ds-title">金蝶发票信息</h4>
          <div class="ds-grid">
            <div class="ds-item">
              <span class="ds-label">发票号码</span>
              <span class="ds-value">{{ invoiceDetail.kingdeeInfo.invoiceNumber || '-' }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">校验码</span>
              <span class="ds-value mono">{{ invoiceDetail.kingdeeInfo.checkCode || '-' }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">发票代码</span>
              <span class="ds-value mono">{{ invoiceDetail.kingdeeInfo.invoiceCode || '-' }}</span>
            </div>
            <div class="ds-item">
              <span class="ds-label">开票机号</span>
              <span class="ds-value">{{ invoiceDetail.kingdeeInfo.machineNo || '-' }}</span>
            </div>
          </div>
          <div v-if="invoiceDetail.kingdeeInfo.pdfUrl" class="ds-item" style="margin-top: 12px">
            <span class="ds-label">发票PDF</span>
            <a :href="invoiceDetail.kingdeeInfo.pdfUrl" target="_blank" class="ds-link">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg>
              下载发票PDF
            </a>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button v-if="invoiceDetail && invoiceDetail.kingdeeInfo && invoiceDetail.kingdeeInfo.pdfUrl" type="primary" @click="downloadPdf">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg> 下载PDF
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getInvoiceDetail, createRedInvoice, deliverInvoice, deleteInvoice, getInvoiceRepayments } from '@/api/invoice'

export default {
  name: 'InvoiceList',
  data() {
    return {
      filterForm: {
        invoiceNo: '',
        customerId: '',
        dateRange: null,
        status: ''
      },
      customers: [],
      invoices: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      summary: {
        totalAmount: 12568000,
        invoicedCount: 326,
        redAmount: 1856000,
        pendingCount: 8
      },
      redDialogVisible: false,
      currentInvoice: null,
      redForm: {
        reason: '',
        amountType: 'full',
        redAmount: '',
        description: ''
      },
      repayments: [],
      detailDialogVisible: false,
      invoiceDetail: null
    }
  },
  mounted() {
    this.loadCustomers()
    this.searchInvoices()
  },
  methods: {
    loadCustomers() {
      this.$http.get('/api/customers').then(res => {
        this.customers = res.data
      })
    },
    searchInvoices() {
      const params = {
        invoiceNo: this.filterForm.invoiceNo,
        customerId: this.filterForm.customerId,
        status: this.filterForm.status,
        page: this.currentPage,
        size: this.pageSize
      }
      if (this.filterForm.dateRange && this.filterForm.dateRange.length === 2) {
        params.startDate = this.filterForm.dateRange[0]
        params.endDate = this.filterForm.dateRange[1]
      }
      this.$http.post('/api/invoices/search', params).then(res => {
        this.invoices = res.data.records
        this.total = res.data.total
      })
    },
    resetFilter() {
      this.filterForm = {
        invoiceNo: '',
        customerId: '',
        dateRange: null,
        status: ''
      }
      this.currentPage = 1
      this.searchInvoices()
    },
    exportInvoices() {
      this.$message.info('导出功能开发中')
    },
    formatNumber(num) {
      return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    getStatusText(status) {
      const map = {
        'DRAFT': '草稿',
        'INVOICED': '已开票',
        'PARTIAL_RED': '部分红冲',
        'RED_INVOICED': '已红冲',
        'PROCESSING': '处理中',
        'FAILED': '开票失败'
      }
      return map[status] || status
    },
    getStatusClass(status) {
      const map = {
        'DRAFT': 'info',
        'INVOICED': 'success',
        'PARTIAL_RED': 'warning',
        'RED_INVOICED': 'danger',
        'PROCESSING': 'info',
        'FAILED': 'danger'
      }
      return map[status] || ''
    },
    showInvoiceDetail(invoice) {
      getInvoiceDetail(invoice.id).then(res => {
        this.invoiceDetail = res.data
        this.detailDialogVisible = true
      }).catch(err => {
        this.$message.error(err.message || '获取发票详情失败')
      })
    },
    downloadPdf() {
      if (this.invoiceDetail && this.invoiceDetail.kingdeeInfo && this.invoiceDetail.kingdeeInfo.pdfUrl) {
        window.open(this.invoiceDetail.kingdeeInfo.pdfUrl, '_blank')
      }
    },
    downloadInvoice(invoice) {
      if (invoice.kingdeeInfo && invoice.kingdeeInfo.pdfUrl) {
        window.open(invoice.kingdeeInfo.pdfUrl, '_blank')
      } else {
        this.$message.info(`下载发票: ${invoice.invoiceNo}`)
      }
    },
    deliverInvoiceAction(invoice) {
      this.$confirm('确认交付此发票吗？交付后发票将标记为已交付状态。', '交付确认', {
        type: 'info',
        confirmButtonText: '确认交付',
        cancelButtonText: '取消'
      }).then(() => {
        deliverInvoice(invoice.id).then(() => {
          this.$message.success('发票交付成功')
          this.searchInvoices()
        }).catch(err => {
          this.$message.error(err.message || '交付失败')
        })
      }).catch(() => {})
    },
    deleteInvoiceAction(invoice) {
      this.$confirm('确认删除此草稿发票吗？删除后无法恢复。', '删除确认', {
        type: 'warning',
        confirmButtonText: '确认删除',
        cancelButtonText: '取消'
      }).then(() => {
        deleteInvoice(invoice.id).then(() => {
          this.$message.success('发票已删除')
          this.searchInvoices()
        }).catch(err => {
          this.$message.error(err.message || '删除失败')
        })
      }).catch(() => {})
    },
    redInvoice(invoice) {
      this.currentInvoice = invoice
      this.redForm = {
        reason: '',
        amountType: 'full',
        redAmount: '',
        description: ''
      }
      this.repayments = []
      this.loadRepayments(invoice.id)
      this.redDialogVisible = true
    },
    loadRepayments(invoiceId) {
      getInvoiceRepayments(invoiceId).then(res => {
        this.repayments = (res.data || []).map(rp => ({
          ...rp,
          checked: true
        }))
      }).catch(() => {
        this.repayments = []
      })
    },
    handleAmountTypeChange(val) {
      if (val === 'full') {
        this.redForm.redAmount = ''
      }
    },
    handleRepaymentCheck() {
      // 当选择还款记录变化时，可在此处计算建议红冲金额
    },
    submitRedInvoice() {
      if (!this.redForm.reason) {
        this.$message.warning('请选择红冲原因')
        return
      }
      if (this.redForm.amountType === 'partial') {
        const amt = parseFloat(this.redForm.redAmount)
        if (!amt || amt <= 0) {
          this.$message.warning('请输入有效的红冲金额')
          return
        }
        if (amt > (this.currentInvoice?.totalAmount || 0)) {
          this.$message.warning('红冲金额不能超过原发票金额')
          return
        }
      }
      if (this.repayments.length > 1) {
        const checkedCount = this.repayments.filter(rp => rp.checked).length
        if (checkedCount === 0) {
          this.$message.warning('请至少选择一条还款记录')
          return
        }
      }
      const submitData = {
        invoiceId: this.currentInvoice.id,
        reason: this.redForm.reason,
        description: this.redForm.description,
        amountType: this.redForm.amountType
      }
      if (this.redForm.amountType === 'partial') {
        submitData.redAmount = parseFloat(this.redForm.redAmount)
      }
      if (this.repayments.length > 1) {
        submitData.repaymentIds = this.repayments.filter(rp => rp.checked).map(rp => rp.id)
      }
      this.$confirm('确认红字冲销此发票吗？冲销后无法撤销。', '提示', {
        type: 'warning'
      }).then(() => {
        createRedInvoice(submitData).then(() => {
          this.$message.success('红字冲销提交成功')
          this.redDialogVisible = false
          this.searchInvoices()
        }).catch(err => {
          this.$message.error(err.message || '提交失败')
        })
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.searchInvoices()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.searchInvoices()
    }
  }
}
</script>

<style scoped>
.invoice-list {
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

.summary-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--fin-card);
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-lg);
  padding: 16px;
}

.sc-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--fin-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.sc-icon.blue {
  background: var(--fin-primary-50);
  color: var(--fin-primary-700);
}

.sc-icon.green {
  background: #ecfdf5;
  color: #059669;
}

.sc-icon.red {
  background: #fef2f2;
  color: #b91c1c;
}

.sc-icon.orange {
  background: #fff7ed;
  color: #ea580c;
}

.sc-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin-bottom: 2px;
}

.sc-label {
  font-size: 12px;
  color: var(--fin-muted-foreground);
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
  grid-template-columns: repeat(4, 1fr);
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

.link-text {
  color: var(--fin-primary-700);
  cursor: pointer;
  text-decoration: underline;
}

.text-right {
  text-align: right;
}

.type-tag {
  padding: 2px 8px;
  border-radius: var(--fin-radius-sm);
  font-size: 11.5px;
  font-weight: 500;
}

.type-tag.blue {
  background: var(--fin-primary-50);
  color: var(--fin-primary-700);
}

.type-tag.red {
  background: #fef2f2;
  color: #b91c1c;
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

.status-tag.danger {
  background: #fef2f2;
  color: #b91c1c;
}

.status-tag.info {
  background: #dbeafe;
  color: #2563eb;
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
  transition: all 0.15s ease;
}

.action-btn:hover {
  background: var(--fin-muted);
}

.action-btn.view:hover {
  color: var(--fin-primary-700);
}

.action-btn.download:hover {
  color: #059669;
}

.action-btn.red-chong:hover {
  color: #b91c1c;
}

.action-btn.deliver:hover {
  color: #2563eb;
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

.red-form {
  padding: 10px 0;
}

.rf-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.rf-label {
  font-size: 12.5px;
  font-weight: 500;
  color: var(--fin-foreground);
  margin-bottom: 6px;
}

.rf-required {
  color: #b91c1c;
  margin-right: 2px;
}

.rf-value {
  font-size: 14px;
  color: var(--fin-foreground);
  font-weight: 500;
}

.rf-amount-row {
  display: flex;
  align-items: center;
  gap: 20px;
}

.rf-amount-hint {
  margin-top: 6px;
  font-size: 12.5px;
  color: var(--fin-muted-foreground);
}

.rf-repayment-tip {
  font-size: 12px;
  color: var(--fin-muted-foreground);
  margin-bottom: 8px;
}

.rf-repayment-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-md);
  font-size: 13px;
}

.rf-repayment-table th {
  background: var(--fin-muted);
  padding: 8px 12px;
  text-align: left;
  font-size: 12px;
  font-weight: 600;
  color: var(--fin-foreground);
}

.rf-repayment-table td {
  padding: 8px 12px;
  border-bottom: 1px solid var(--fin-border);
  color: var(--fin-foreground);
}

.rf-repayment-table tbody tr:last-child td {
  border-bottom: none;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 发票详情对话框 */
.detail-content {
  padding: 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.ds-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin: 0 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--fin-border);
}

.ds-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 24px;
}

.ds-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ds-label {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.ds-value {
  font-size: 14px;
  color: var(--fin-foreground);
  font-weight: 500;
}

.ds-value.amount {
  color: var(--fin-primary-700);
  font-weight: 600;
}

.ds-value.mono {
  font-family: 'Courier New', Courier, monospace;
  letter-spacing: 0.5px;
}

.ds-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--fin-primary-700);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.ds-link:hover {
  text-decoration: underline;
}

@media (max-width: 1200px) {
  .summary-bar {
    grid-template-columns: repeat(2, 1fr);
  }
  .filter-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .summary-bar {
    grid-template-columns: 1fr;
  }
  .filter-row {
    grid-template-columns: 1fr;
  }
  .ds-grid {
    grid-template-columns: 1fr;
  }
}
</style>
