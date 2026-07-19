<template>
  <div class="blue-invoice">
    <el-card title="蓝字开票">
      <el-form ref="invoiceForm" :model="invoiceForm" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerId">
              <el-select v-model="invoiceForm.customerId" placeholder="请选择客户" @change="handleCustomerChange">
                <el-option v-for="customer in customers" :key="customer.id" :label="customer.name" :value="customer.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发票类型" prop="invoiceType">
              <el-select v-model="invoiceForm.invoiceType" placeholder="请选择发票类型">
                <el-option label="增值税专用发票" value="SPECIAL"></el-option>
                <el-option label="增值税普通发票" value="NORMAL"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开票日期">
              <el-date-picker v-model="invoiceForm.issueDate" type="date" placeholder="选择日期"></el-date-picker>
            </el-form-item>
            <el-col :span="12">
              <el-form-item label="备注">
                <el-input v-model="invoiceForm.remark" placeholder="请输入备注"></el-input>
              </el-form-item>
            </el-col>
          </el-col>
        </el-row>
        <el-divider></el-divider>
        <div class="table-header">
          <span>发票明细</span>
          <el-button type="primary" size="small" @click="addDetail">添加明细</el-button>
        </div>
        <el-table :data="invoiceForm.details" border>
          <el-table-column prop="contractId" label="合同编号">
            <template slot-scope="scope">
              <el-select v-model="scope.row.contractId" placeholder="请选择合同">
                <el-option v-for="contract in contracts" :key="contract.id" :label="contract.contractNo" :value="contract.id"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="repaymentRecordId" label="还款记录号">
            <template slot-scope="scope">
              <el-select v-model="scope.row.repaymentRecordId" placeholder="请选择还款记录">
                <el-option v-for="record in repaymentRecords" :key="record.id" :label="record.recordNo" :value="record.id"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="feeType" label="费用类型">
            <template slot-scope="scope">
              <el-select v-model="scope.row.feeType">
                <el-option label="利息" value="INTEREST"></el-option>
                <el-option label="手续费" value="FEE"></el-option>
                <el-option label="罚息" value="PENALTY"></el-option>
                <el-option label="承诺费" value="COMMITMENT"></el-option>
                <el-option label="其他" value="OTHER"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="金额">
            <template slot-scope="scope">
              <el-input v-model.number="scope.row.totalAmount" type="number"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="interestAmount" label="利息金额">
            <template slot-scope="scope">
              <el-input v-model.number="scope.row.interestAmount" type="number"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="taxRate" label="税率(%)">
            <template slot-scope="scope">
              <el-input v-model.number="scope.row.taxRate" type="number" :disabled="true"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="项目描述">
            <template slot-scope="scope">
              <el-input v-model="scope.row.description" placeholder="请输入项目描述"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="8">
            <div class="summary-item">
              <span class="summary-label">总金额:</span>
              <span class="summary-value">{{ totalAmount.toFixed(2) }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="summary-item">
              <span class="summary-label">税额:</span>
              <span class="summary-value">{{ taxAmount.toFixed(2) }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="summary-item">
              <span class="summary-label">价税合计:</span>
              <span class="summary-value total">{{ totalAmount.toFixed(2) }}</span>
            </div>
          </el-col>
        </el-row>
        <el-form-item style="margin-top: 20px;">
          <el-button type="primary" @click="handleSubmit">提交审核</el-button>
          <el-button @click="handleSaveDraft">保存草稿</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getAllCustomers } from '@/api/customer'
import { getAllContracts } from '@/api/contract'
import { getAllRecords } from '@/api/repayment'
import { createInvoice, submitForReview } from '@/api/invoice'

export default {
  name: 'BlueInvoice',
  data() {
    return {
      invoiceForm: {
        customerId: '',
        invoiceType: '',
        issueDate: new Date(),
        remark: '',
        details: []
      },
      rules: {
        customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
        invoiceType: [{ required: true, message: '请选择发票类型', trigger: 'change' }]
      },
      customers: [],
      contracts: [],
      repaymentRecords: []
    }
  },
  computed: {
    totalAmount() {
      return this.invoiceForm.details.reduce((sum, item) => sum + (item.totalAmount || 0), 0)
    },
    taxAmount() {
      return this.invoiceForm.details.reduce((sum, item) => {
        const interest = item.interestAmount || 0
        const rate = item.taxRate || 6
        return sum + (interest * rate / 100)
      }, 0)
    }
  },
  mounted() {
    this.loadCustomers()
  },
  methods: {
    loadCustomers() {
      getAllCustomers().then(res => {
        this.customers = res.data
      })
    },
    handleCustomerChange(customerId) {
      getAllContracts({ customerId }).then(res => {
        this.contracts = res.data
      })
      getAllRecords({ customerId }).then(res => {
        this.repaymentRecords = res.data
      })
    },
    addDetail() {
      this.invoiceForm.details.push({
        contractId: '',
        repaymentRecordId: '',
        feeType: 'INTEREST',
        totalAmount: 0,
        interestAmount: 0,
        taxRate: 6,
        description: ''
      })
    },
    removeDetail(index) {
      this.invoiceForm.details.splice(index, 1)
    },
    handleSubmit() {
      this.$refs.invoiceForm.validate((valid) => {
        if (valid && this.invoiceForm.details.length > 0) {
          createInvoice(this.invoiceForm).then(res => {
            const invoiceId = res.data.id
            submitForReview(invoiceId).then(() => {
              this.$message.success('提交审核成功')
              this.resetForm()
            })
          })
        } else if (this.invoiceForm.details.length === 0) {
          this.$message.warning('请添加发票明细')
        }
      })
    },
    handleSaveDraft() {
      if (this.invoiceForm.details.length === 0) {
        this.$message.warning('请添加发票明细')
        return
      }
      createInvoice(this.invoiceForm).then(() => {
        this.$message.success('保存草稿成功')
        this.resetForm()
      })
    },
    resetForm() {
      this.invoiceForm = {
        customerId: '',
        invoiceType: '',
        issueDate: new Date(),
        remark: '',
        details: []
      }
    }
  }
}
</script>

<style scoped>
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background-color: #f9fafc;
  border-radius: 4px;
}

.summary-label {
  color: #909399;
}

.summary-value {
  font-weight: bold;
  color: #303133;
}

.summary-value.total {
  color: #F56C6C;
}
</style>