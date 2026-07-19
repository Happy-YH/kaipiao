<template>
  <div class="batch-invoice">
    <div class="page-header">
      <h1 class="ph-title">手工开票</h1>
      <p class="ph-desc">通过导入Excel文件批量开具发票</p>
    </div>

    <div class="card upload-card">
      <div class="card-header">
        <h3 class="ch-title">文件上传</h3>
        <button class="ch-action" @click="downloadTemplate">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg>
          下载模板
        </button>
      </div>
      <div class="upload-area" :class="{ dragging: isDragging }" @dragover.prevent="isDragging = true" @dragleave="isDragging = false" @drop.prevent="handleDrop">
        <div class="upload-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" x2="12" y1="3" y2="15"/></svg>
        </div>
        <div class="upload-text">
          <div class="ut-title">点击或拖拽文件到此处上传</div>
          <div class="ut-desc">支持 .xlsx 格式，单文件最大 10MB</div>
        </div>
        <input type="file" class="upload-input" accept=".xlsx,.xls" @change="handleFileSelect">
      </div>
      <div v-if="uploadedFile" class="file-info">
        <div class="fi-left">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
          <span class="fi-name">{{ uploadedFile.name }}</span>
        </div>
        <div class="fi-right">
          <span class="fi-size">{{ formatFileSize(uploadedFile.size) }}</span>
          <button class="fi-remove" @click="removeFile">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
          </button>
        </div>
      </div>
    </div>

    <div class="card rules-card">
      <div class="card-header">
        <h3 class="ch-title">校验规则</h3>
      </div>
      <div class="rules-grid">
        <div class="rule-item">
          <div class="ri-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="ri-info">
            <div class="ri-title">客户信息校验</div>
            <div class="ri-desc">自动校验客户名称、税号是否在系统中存在</div>
          </div>
        </div>
        <div class="rule-item">
          <div class="ri-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="ri-info">
            <div class="ri-title">金额格式校验</div>
            <div class="ri-desc">验证金额是否为有效数字，支持两位小数</div>
          </div>
        </div>
        <div class="rule-item">
          <div class="ri-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="ri-info">
            <div class="ri-title">税率匹配校验</div>
            <div class="ri-desc">验证税率是否在系统配置的税率范围内</div>
          </div>
        </div>
        <div class="rule-item">
          <div class="ri-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="ri-info">
            <div class="ri-title">必填项校验</div>
            <div class="ri-desc">检查客户名称、金额、税率等必填字段是否为空</div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="importResult" class="card result-card">
      <div class="card-header">
        <h3 class="ch-title">导入结果</h3>
        <div class="ch-right">
          <div class="result-summary">
            <span class="rs-item success">成功: {{ importResult.successCount }}</span>
            <span class="rs-item error">失败: {{ importResult.errorCount }}</span>
            <span class="rs-item total">总计: {{ importResult.totalCount }}</span>
          </div>
        </div>
      </div>
      <div class="result-tabs">
        <button class="rt-btn" :class="{ active: activeTab === 'success' }" @click="activeTab = 'success'">成功列表</button>
        <button class="rt-btn" :class="{ active: activeTab === 'error' }" @click="activeTab = 'error'">失败列表</button>
      </div>
      <div class="table-wrapper">
        <table v-if="activeTab === 'success'" class="data-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>客户名称</th>
              <th>税号</th>
              <th>合同编号</th>
              <th>金额</th>
              <th>税率</th>
              <th>税额</th>
              <th>价税合计</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in importResult.successList" :key="index">
              <td>{{ index + 1 }}</td>
              <td>{{ item.customerName }}</td>
              <td>{{ item.taxNo }}</td>
              <td>{{ item.contractNo }}</td>
              <td class="text-right">¥ {{ formatNumber(item.amount) }}</td>
              <td>{{ (item.taxRate * 100).toFixed(1) }}%</td>
              <td class="text-right">¥ {{ formatNumber(item.taxAmount) }}</td>
              <td class="text-right">¥ {{ formatNumber(item.totalAmount) }}</td>
            </tr>
          </tbody>
        </table>
        <table v-else class="data-table">
          <thead>
            <tr>
              <th>行号</th>
              <th>客户名称</th>
              <th>金额</th>
              <th>税率</th>
              <th>错误原因</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in importResult.errorList" :key="index">
              <td>{{ item.rowNo }}</td>
              <td>{{ item.customerName || '-' }}</td>
              <td>{{ item.amount || '-' }}</td>
              <td>{{ item.taxRate || '-' }}</td>
              <td><span class="error-text">{{ item.errorMessage }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="bottom-bar">
      <div class="bb-left">
        <div class="summary-item">
          <span class="si-label">待开票金额合计</span>
          <span class="si-value">¥ {{ formatNumber(totalAmount) }}</span>
        </div>
        <div class="summary-item">
          <span class="si-label">税额合计</span>
          <span class="si-value">¥ {{ formatNumber(totalTaxAmount) }}</span>
        </div>
        <div class="summary-item">
          <span class="si-label">价税合计</span>
          <span class="si-value highlight">¥ {{ formatNumber(totalAmount + totalTaxAmount) }}</span>
        </div>
      </div>
      <div class="bb-right">
        <el-button @click="validateData">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg> 数据校验
        </el-button>
        <el-button type="primary" :disabled="!uploadedFile || !importResult" @click="submitBatch">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg> 提交开票
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BatchInvoice',
  data() {
    return {
      isDragging: false,
      uploadedFile: null,
      importResult: null,
      activeTab: 'success'
    }
  },
  computed: {
    totalAmount() {
      if (!this.importResult) return 0
      return this.importResult.successList.reduce((sum, item) => sum + item.amount, 0)
    },
    totalTaxAmount() {
      if (!this.importResult) return 0
      return this.importResult.successList.reduce((sum, item) => sum + item.taxAmount, 0)
    }
  },
  methods: {
    handleDrop(e) {
      this.isDragging = false
      const files = e.dataTransfer.files
      if (files.length > 0) {
        this.handleFile(files[0])
      }
    },
    handleFileSelect(e) {
      const files = e.target.files
      if (files.length > 0) {
        this.handleFile(files[0])
      }
    },
    handleFile(file) {
      const ext = file.name.split('.').pop().toLowerCase()
      if (ext !== 'xlsx' && ext !== 'xls') {
        this.$message.error('请上传 Excel 文件（.xlsx 或 .xls 格式）')
        return
      }
      if (file.size > 10 * 1024 * 1024) {
        this.$message.error('文件大小不能超过 10MB')
        return
      }
      this.uploadedFile = file
      this.importResult = null
    },
    removeFile() {
      this.uploadedFile = null
      this.importResult = null
    },
    downloadTemplate() {
      this.$message.info('模板下载功能开发中')
    },
    formatFileSize(bytes) {
      if (bytes < 1024) return bytes + ' B'
      if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
      return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
    },
    formatNumber(num) {
      if (num === null || num === undefined || num === '') return '0.00'
      return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    validateData() {
      if (!this.uploadedFile) {
        this.$message.warning('请先上传文件')
        return
      }
      this.$message.loading('正在校验数据...', 1.5).then(() => {
        this.importResult = {
          totalCount: 15,
          successCount: 12,
          errorCount: 3,
          successList: [
            { customerName: '北京科技有限公司', taxNo: '91110101MA01ABCDE', contractNo: 'HT202603001', amount: 125000, taxRate: 0.06, taxAmount: 7500, totalAmount: 132500 },
            { customerName: '上海投资集团', taxNo: '91310101MA02FGHIJ', contractNo: 'HT202603002', amount: 89000, taxRate: 0.06, taxAmount: 5340, totalAmount: 94340 },
            { customerName: '深圳金融服务公司', taxNo: '91440301MA03KLMNO', contractNo: 'HT202603003', amount: 234000, taxRate: 0.05, taxAmount: 11700, totalAmount: 245700 },
            { customerName: '广州贸易有限公司', taxNo: '91440101MA04PQRS', contractNo: 'HT202603004', amount: 67000, taxRate: 0.06, taxAmount: 4020, totalAmount: 71020 },
            { customerName: '杭州科技有限公司', taxNo: '91330101MA05TUVW', contractNo: 'HT202603005', amount: 156000, taxRate: 0.06, taxAmount: 9360, totalAmount: 165360 },
            { customerName: '成都投资集团', taxNo: '91510101MA06XYZ', contractNo: 'HT202603006', amount: 98000, taxRate: 0.05, taxAmount: 4900, totalAmount: 102900 },
            { customerName: '武汉金融服务公司', taxNo: '91420101MA07ABCD', contractNo: 'HT202603007', amount: 178000, taxRate: 0.06, taxAmount: 10680, totalAmount: 188680 },
            { customerName: '南京科技有限公司', taxNo: '91320101MA08EFGH', contractNo: 'HT202603008', amount: 112000, taxRate: 0.06, taxAmount: 6720, totalAmount: 118720 },
            { customerName: '苏州贸易有限公司', taxNo: '91320501MA09IJKL', contractNo: 'HT202603009', amount: 145000, taxRate: 0.05, taxAmount: 7250, totalAmount: 152250 },
            { customerName: '厦门投资集团', taxNo: '91350201MA10MNOP', contractNo: 'HT202603010', amount: 87000, taxRate: 0.06, taxAmount: 5220, totalAmount: 92220 },
            { customerName: '青岛金融服务公司', taxNo: '91370201MA11QRST', contractNo: 'HT202603011', amount: 134000, taxRate: 0.06, taxAmount: 8040, totalAmount: 142040 },
            { customerName: '大连科技有限公司', taxNo: '91210201MA12UVWX', contractNo: 'HT202603012', amount: 95000, taxRate: 0.05, taxAmount: 4750, totalAmount: 99750 }
          ],
          errorList: [
            { rowNo: 5, customerName: '未知客户', amount: 'abc', taxRate: '0.06', errorMessage: '金额格式错误，不是有效数字' },
            { rowNo: 8, customerName: '', amount: '120000', taxRate: '0.06', errorMessage: '客户名称不能为空' },
            { rowNo: 12, customerName: '测试公司', amount: '80000', taxRate: '0.15', errorMessage: '税率 15% 不在系统配置范围内' }
          ]
        }
        if (this.importResult.errorCount > 0) {
          this.$message.warning(`数据校验完成，${this.importResult.successCount} 条通过，${this.importResult.errorCount} 条失败`)
        } else {
          this.$message.success(`数据校验完成，全部 ${this.importResult.successCount} 条通过`)
        }
      })
    },
    submitBatch() {
      this.$confirm('确认提交批量开票申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$message.loading('正在提交...', 1.5).then(() => {
          this.$message.success('批量开票申请提交成功')
          this.uploadedFile = null
          this.importResult = null
        })
      })
    }
  }
}
</script>

<style scoped>
.batch-invoice {
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

.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
  margin: 20px;
  border: 2px dashed var(--fin-border);
  border-radius: var(--fin-radius-lg);
  background: var(--fin-muted);
  cursor: pointer;
  transition: all 0.2s ease;
}

.upload-area:hover {
  border-color: var(--fin-primary-400);
  background: var(--fin-primary-50);
}

.upload-area.dragging {
  border-color: var(--fin-primary-600);
  background: var(--fin-primary-50);
}

.upload-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--fin-radius-lg);
  background: var(--fin-card);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--fin-primary-600);
  margin-bottom: 16px;
}

.upload-text {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.ut-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--fin-foreground);
  margin-bottom: 6px;
}

.ut-desc {
  font-size: 12.5px;
  color: var(--fin-muted-foreground);
}

.upload-input {
  display: none;
}

.file-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  margin: 0 20px 20px;
  background: var(--fin-muted);
  border-radius: var(--fin-radius-md);
}

.fi-left {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--fin-primary-700);
}

.fi-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--fin-foreground);
}

.fi-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.fi-size {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.fi-remove {
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

.fi-remove:hover {
  background: rgba(185, 28, 28, 0.1);
  color: #b91c1c;
}

.rules-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 20px;
}

.rule-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: var(--fin-muted);
  border-radius: var(--fin-radius-md);
}

.ri-icon {
  width: 28px;
  height: 28px;
  border-radius: var(--fin-radius-sm);
  background: #ecfdf5;
  color: #059669;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ri-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--fin-foreground);
  margin-bottom: 3px;
}

.ri-desc {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.result-tabs {
  display: flex;
  border-bottom: 1px solid var(--fin-border);
}

.rt-btn {
  padding: 10px 24px;
  border: none;
  background: transparent;
  font-size: 13px;
  color: var(--fin-muted-foreground);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.15s ease;
}

.rt-btn.active {
  color: var(--fin-primary-700);
  border-bottom-color: var(--fin-primary-700);
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

.error-text {
  color: #b91c1c;
}

.result-summary {
  display: flex;
  gap: 20px;
}

.rs-item {
  font-size: 12px;
  font-weight: 500;
}

.rs-item.success {
  color: #059669;
}

.rs-item.error {
  color: #b91c1c;
}

.rs-item.total {
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
  .rules-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
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