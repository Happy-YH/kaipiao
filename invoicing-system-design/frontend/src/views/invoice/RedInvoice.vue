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
            <el-form-item label="冲销原因" prop="reason">
              <el-select v-model="redForm.reason" placeholder="请选择冲销原因">
                <el-option label="开票有误" value="WRONG_INFO"></el-option>
                <el-option label="销货退回" value="RETURN"></el-option>
                <el-option label="销售折让" value="DISCOUNT"></el-option>
                <el-option label="其他" value="OTHER"></el-option>
              </el-select>
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
        <el-form-item style="margin-top: 20px;">
          <el-button type="danger" @click="handleSubmit">提交冲销申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getInvoiceByNo } from '@/api/invoice'
import { createRedConfirmation } from '@/api/redConfirmation'

export default {
  name: 'RedInvoice',
  data() {
    return {
      redForm: {
        originalInvoiceNo: '',
        originalInvoiceCode: '',
        originalAmount: '',
        reason: '',
        description: ''
      },
      rules: {
        originalInvoiceNo: [{ required: true, message: '请输入原发票号码', trigger: 'blur' }],
        reason: [{ required: true, message: '请选择冲销原因', trigger: 'change' }]
      },
      originalInvoice: null
    }
  },
  methods: {
    searchOriginalInvoice() {
      if (!this.redForm.originalInvoiceNo) return
      getInvoiceByNo(this.redForm.originalInvoiceNo).then(res => {
        this.originalInvoice = res.data
        this.redForm.originalInvoiceCode = res.data.invoiceCode
        this.redForm.originalAmount = res.data.totalAmount
      }).catch(() => {
        this.$message.warning('未找到该发票')
        this.originalInvoice = null
        this.redForm.originalInvoiceCode = ''
        this.redForm.originalAmount = ''
      })
    },
    handleSubmit() {
      this.$refs.redForm.validate((valid) => {
        if (valid && this.originalInvoice) {
          const requestData = {
            invoiceId: this.originalInvoice.id,
            reason: this.redForm.reason,
            description: this.redForm.description
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
        reason: '',
        description: ''
      }
      this.originalInvoice = null
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
</style>