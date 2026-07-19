<template>
  <div class="red-invoice">
    <el-card title="红字冲销">
      <el-form ref="redForm" :model="redForm" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原发票号码" prop="originalInvoiceNo">
              <el-input v-model="redForm.originalInvoiceNo" placeholder="请输入原发票号码" @blur="searchOriginalInvoice"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原发票代码">
              <el-input v-model="redForm.originalInvoiceCode" :disabled="true"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原发票金额">
              <el-input v-model="redForm.originalAmount" :disabled="true"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="红冲类型" prop="cancelType">
              <el-select v-model="redForm.cancelType" placeholder="请选择红冲类型" @change="handleCancelTypeChange">
                <el-option label="全额红冲" value="FULL"></el-option>
                <el-option label="部分红冲" value="PARTIAL"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="冲销原因" prop="cancelReason">
              <el-select v-model="redForm.cancelReason" placeholder="请选择冲销原因">
                <el-option label="开票有误" value="WRONG_INFO"></el-option>
                <el-option label="销货退回" value="RETURN"></el-option>
                <el-option label="销售折让" value="DISCOUNT"></el-option>
                <el-option label="其他" value="OTHER"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="redForm.cancelType === 'PARTIAL'">
            <el-form-item label="冲销金额" prop="cancelAmount">
              <el-input v-model.number="redForm.cancelAmount" type="number" placeholder="请输入冲销金额"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="冲销说明">
          <el-input v-model="redForm.description" type="textarea" :rows="3" placeholder="请输入冲销说明"></el-input>
        </el-form-item>
        <el-divider></el-divider>

        <div v-if="originalInvoice" class="original-info">
          <h4>原发票信息</h4>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="发票号码">{{ originalInvoice.invoiceNo }}</el-descriptions-item>
            <el-descriptions-item label="发票代码">{{ originalInvoice.invoiceCode }}</el-descriptions-item>
            <el-descriptions-item label="发票类型">{{ getInvoiceTypeName(originalInvoice.invoiceType) }}</el-descriptions-item>
            <el-descriptions-item label="客户名称">{{ originalInvoice.customerName }}</el-descriptions-item>
            <el-descriptions-item label="开票日期">{{ formatDate(originalInvoice.issueDate) }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ getStatusName(originalInvoice.status) }}</el-descriptions-item>
            <el-descriptions-item label="总金额" :span="3">{{ originalInvoice.totalAmount }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="redForm.cancelType === 'PARTIAL' && originalInvoice" style="margin-top: 20px;">
          <div class="table-header">
            <span>选择需红冲的还款记录</span>
            <span class="header-tip">1:1 场景：选择单笔还款记录按差额重开；1:N 场景：选择多笔还款记录按未冲销记录分别重开</span>
          </div>
          <el-table :data="repaymentRecords" border @selection-change="handleSelectionChange" ref="repaymentTable">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="recordNo" label="还款记录号"></el-table-column>
            <el-table-column prop="repaymentDate" label="还款日期">
              <template slot-scope="scope">{{ formatDate(scope.row.repaymentDate) }}</template>
            </el-table-column>
            <el-table-column prop="principalAmount" label="本金金额"></el-table-column>
            <el-table-column prop="interestAmount" label="利息金额"></el-table-column>
            <el-table-column prop="taxableAmount" label="可开票金额"></el-table-column>
            <el-table-column prop="invoicedAmount" label="已开票金额"></el-table-column>
            <el-table-column prop="invoiceStatus" label="开票状态">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.invoiceStatus)">
                  {{ getStatusName(scope.row.invoiceStatus) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="selection-summary" v-if="selectedRecords.length > 0">
            <span>已选择 <strong>{{ selectedRecords.length }}</strong> 条记录，</span>
            <span>冲销金额合计：<strong>{{ selectedCancelAmount.toFixed(2) }}</strong></span>
          </div>
        </div>

        <el-form-item style="margin-top: 20px;">
          <el-button type="danger" @click="handleSubmit">提交冲销申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getInvoiceByNo, getInvoiceRepayments } from '@/api/invoice'
import { createRedConfirmation } from '@/api/redConfirmation'

export default {
  name: 'RedInvoice',
  data() {
    const validateCancelAmount = (rule, value, callback) => {
      if (this.redForm.cancelType === 'PARTIAL') {
        if (!value || value <= 0) {
          callback(new Error('部分红冲请输入大于0的冲销金额'))
        } else if (this.originalInvoice && value > Number(this.originalInvoice.totalAmount)) {
          callback(new Error('冲销金额不能超过原发票金额'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    return {
      redForm: {
        originalInvoiceNo: '',
        originalInvoiceCode: '',
        originalAmount: '',
        cancelType: 'FULL',
        cancelReason: '',
        cancelAmount: 0,
        description: ''
      },
      rules: {
        originalInvoiceNo: [{ required: true, message: '请输入原发票号码', trigger: 'blur' }],
        cancelType: [{ required: true, message: '请选择红冲类型', trigger: 'change' }],
        cancelReason: [{ required: true, message: '请选择冲销原因', trigger: 'change' }],
        cancelAmount: [{ validator: validateCancelAmount, trigger: 'blur' }]
      },
      originalInvoice: null,
      repaymentRecords: [],
      selectedRecords: []
    }
  },
  computed: {
    selectedCancelAmount() {
      return this.selectedRecords.reduce((sum, r) => sum + (Number(r.taxableAmount) || 0), 0)
    }
  },
  methods: {
    searchOriginalInvoice() {
      if (!this.redForm.originalInvoiceNo) return
      getInvoiceByNo(this.redForm.originalInvoiceNo).then(res => {
        const inv = res.data
        if (!inv) {
          this.$message.warning('未找到该发票')
          this.originalInvoice = null
          return
        }
        const allowed = ['ISSUED', 'DELIVERED']
        if (allowed.indexOf(inv.status) === -1) {
          this.$message.warning('该发票状态不允许红冲（仅已开具/已交付状态可红冲）')
        }
        this.originalInvoice = inv
        this.redForm.originalInvoiceCode = inv.invoiceCode
        this.redForm.originalAmount = inv.totalAmount
        if (this.redForm.cancelType === 'PARTIAL') {
          this.loadRepaymentRecords(inv.id)
        }
      }).catch(() => {
        this.$message.warning('未找到该发票')
        this.originalInvoice = null
        this.redForm.originalInvoiceCode = ''
        this.redForm.originalAmount = ''
        this.repaymentRecords = []
      })
    },
    loadRepaymentRecords(invoiceId) {
      getInvoiceRepayments(invoiceId).then(res => {
        this.repaymentRecords = res.data || []
      }).catch(() => {
        this.repaymentRecords = []
      })
    },
    handleCancelTypeChange(val) {
      if (val === 'PARTIAL' && this.originalInvoice) {
        this.loadRepaymentRecords(this.originalInvoice.id)
      } else {
        this.repaymentRecords = []
        this.selectedRecords = []
      }
    },
    handleSelectionChange(val) {
      this.selectedRecords = val
      if (val.length > 0) {
        const sum = val.reduce((s, r) => s + (Number(r.taxableAmount) || 0), 0)
        this.redForm.cancelAmount = Number(sum.toFixed(2))
      }
    },
    handleSubmit() {
      this.$refs.redForm.validate((valid) => {
        if (valid && this.originalInvoice) {
          if (this.redForm.cancelType === 'PARTIAL' && this.selectedRecords.length === 0) {
            this.$message.warning('部分红冲请至少选择一条还款记录')
            return
          }
          const requestData = {
            invoiceId: this.originalInvoice.id,
            cancelType: this.redForm.cancelType,
            cancelReason: this.redForm.cancelReason,
            cancelAmount: this.redForm.cancelType === 'PARTIAL' ? this.redForm.cancelAmount : null,
            remark: this.redForm.description,
            redRepaymentRecordIds: this.selectedRecords.length > 0
              ? this.selectedRecords.map(r => r.id).join(',')
              : null
          }
          createRedConfirmation(requestData).then(() => {
            this.$message.success('冲销申请提交成功')
            this.resetForm()
          })
        } else if (!this.originalInvoice) {
          this.$message.warning('请先查询原发票')
        }
      })
    },
    resetForm() {
      this.redForm = {
        originalInvoiceNo: '',
        originalInvoiceCode: '',
        originalAmount: '',
        cancelType: 'FULL',
        cancelReason: '',
        cancelAmount: 0,
        description: ''
      }
      this.originalInvoice = null
      this.repaymentRecords = []
      this.selectedRecords = []
    },
    getInvoiceTypeName(type) {
      const types = { 'SPECIAL': '增值税专用发票', 'NORMAL': '增值税普通发票' }
      return types[type] || type
    },
    getStatusName(status) {
      const statuses = {
        'DRAFT': '草稿', 'PENDING_REVIEW': '待审核', 'ISSUED': '已开具',
        'DELIVERED': '已交付', 'RED_CANCELLED': '已红冲'
      }
      return statuses[status] || status
    },
    getStatusType(status) {
      const types = {
        'UNINVOICED': 'danger', 'INVOICED': 'success',
        'PARTIAL_INVOICED': 'warning', 'RED_CANCELLED': 'info'
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
.original-info {
  margin-bottom: 20px;
}

.original-info h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.header-tip {
  font-size: 12px;
  color: #909399;
}

.selection-summary {
  margin-top: 10px;
  padding: 10px 15px;
  background-color: #f4f4f5;
  border-radius: 4px;
  color: #606266;
  font-size: 13px;
}

.selection-summary strong {
  color: #F56C6C;
  margin: 0 4px;
}
</style>
