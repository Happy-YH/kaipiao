<template>
  <div class="tax-classifications">
    <div class="page-header">
      <h1 class="ph-title">税目管理</h1>
      <p class="ph-desc">管理发票税目信息，配置税率和费用类型</p>
    </div>

    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label class="filter-label">税目代码</label>
          <el-input v-model="searchForm.taxCode" placeholder="请输入税目代码"></el-input>
        </div>
        <div class="filter-item">
          <label class="filter-label">费用类型</label>
          <el-select v-model="searchForm.feeType" placeholder="全部类型" style="width: 100%">
            <el-option label="利息" value="INTEREST"></el-option>
            <el-option label="手续费" value="FEE"></el-option>
            <el-option label="罚息" value="PENALTY"></el-option>
            <el-option label="承诺费" value="COMMITMENT"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <label class="filter-label">状态</label>
          <el-select v-model="searchForm.status" placeholder="全部状态" style="width: 100%">
            <el-option label="启用" value="ACTIVE"></el-option>
            <el-option label="停用" value="INACTIVE"></el-option>
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
        <el-button type="success" @click="handleAdd">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg> 新增税目
        </el-button>
      </div>
    </div>

    <div class="card data-card">
      <div class="card-header">
        <h3 class="ch-title">税目列表</h3>
        <div class="ch-right">
          <span class="ch-sub">共 {{ total }} 条记录</span>
        </div>
      </div>
      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>税目代码</th>
              <th>税目名称</th>
              <th>费用类型</th>
              <th>税率(%)</th>
              <th>描述</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in tableData" :key="item.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>{{ item.taxCode }}</td>
              <td class="cell-link">{{ item.taxName }}</td>
              <td>
                <span class="status-tag" :class="getFeeTypeClass(item.feeType)">
                  {{ getFeeTypeName(item.feeType) }}
                </span>
              </td>
              <td class="col-amount">{{ item.taxRate }}%</td>
              <td class="ellipsis">{{ item.description }}</td>
              <td>
                <span class="status-tag" :class="item.status === 'ACTIVE' ? 'success' : 'neutral'">
                  {{ item.status === 'ACTIVE' ? '启用' : '停用' }}
                </span>
              </td>
              <td>
                <div class="action-group">
                  <button class="action-btn edit" title="编辑" @click="handleEdit(item)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button class="action-btn toggle" title="切换状态" @click="handleToggle(item)">
                    <svg v-if="item.status === 'ACTIVE'" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
                    <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="16"/></svg>
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

    <el-dialog title="税目信息" :visible.sync="dialogVisible" width="500px">
      <el-form ref="taxForm" :model="taxForm" :rules="rules" label-width="100px">
        <el-form-item label="税目代码" prop="taxCode">
          <el-input v-model="taxForm.taxCode" placeholder="请输入税目代码"></el-input>
        </el-form-item>
        <el-form-item label="税目名称" prop="taxName">
          <el-input v-model="taxForm.taxName" placeholder="请输入税目名称"></el-input>
        </el-form-item>
        <el-form-item label="费用类型" prop="feeType">
          <el-select v-model="taxForm.feeType" placeholder="请选择费用类型" style="width: 100%">
            <el-option label="利息" value="INTEREST"></el-option>
            <el-option label="手续费" value="FEE"></el-option>
            <el-option label="罚息" value="PENALTY"></el-option>
            <el-option label="承诺费" value="COMMITMENT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="税率(%)" prop="taxRate">
          <el-input v-model.number="taxForm.taxRate" type="number" placeholder="请输入税率"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="taxForm.description" type="textarea" :rows="2" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TaxClassifications',
  data() {
    return {
      searchForm: {
        taxCode: '',
        feeType: '',
        status: ''
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      isEdit: false,
      taxForm: {
        id: '',
        taxCode: '',
        taxName: '',
        feeType: 'INTEREST',
        taxRate: 0,
        description: '',
        status: 'ACTIVE'
      },
      rules: {
        taxCode: [{ required: true, message: '请输入税目代码', trigger: 'blur' }],
        taxName: [{ required: true, message: '请输入税目名称', trigger: 'blur' }],
        taxRate: [{ required: true, message: '请输入税率', trigger: 'blur' }],
        feeType: [{ required: true, message: '请选择费用类型', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$http.post('/api/tax-classifications/search', {
        ...this.searchForm,
        page: this.currentPage,
        size: this.pageSize
      }).then(res => {
        this.tableData = res.data.records || res.data
        this.total = res.data.total || this.tableData.length
      }).catch(() => {
        this.tableData = [
          { id: 1, taxCode: '30601', taxName: '贷款服务', feeType: 'INTEREST', taxRate: 6, description: '各种占用、拆借资金取得的收入', status: 'ACTIVE' },
          { id: 2, taxCode: '30602', taxName: '直接收费金融服务', feeType: 'FEE', taxRate: 6, description: '提供货币兑换等金融服务', status: 'ACTIVE' },
          { id: 3, taxCode: '30603', taxName: '保险服务', feeType: 'OTHER', taxRate: 6, description: '人身保险和财产保险服务', status: 'ACTIVE' },
          { id: 4, taxCode: '30604', taxName: '金融商品转让', feeType: 'OTHER', taxRate: 6, description: '转让金融商品所有权', status: 'INACTIVE' }
        ]
        this.total = this.tableData.length
      })
    },
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { taxCode: '', feeType: '', status: '' }
      this.currentPage = 1
      this.loadData()
    },
    handleAdd() {
      this.isEdit = false
      this.taxForm = {
        id: '',
        taxCode: '',
        taxName: '',
        feeType: 'INTEREST',
        taxRate: 0,
        description: '',
        status: 'ACTIVE'
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.taxForm = { ...row }
      this.dialogVisible = true
    },
    handleToggle(row) {
      const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      const statusText = newStatus === 'ACTIVE' ? '启用' : '停用'
      this.$confirm(`确定要${statusText}税目「${row.taxName}」吗？`, '提示', { type: 'warning' }).then(() => {
        this.$http.put(`/api/tax-classifications/${row.id}/status`, { status: newStatus }).then(() => {
          this.$message.success(`${statusText}成功`)
          this.loadData()
        }).catch(() => {
          row.status = row.status === 'ACTIVE' ? 'ACTIVE' : 'INACTIVE'
        })
      })
    },
    handleSave() {
      this.$refs.taxForm.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            this.$http.put('/api/tax-classifications', this.taxForm).then(() => {
              this.$message.success('更新成功')
              this.dialogVisible = false
              this.loadData()
            })
          } else {
            this.$http.post('/api/tax-classifications', this.taxForm).then(() => {
              this.$message.success('创建成功')
              this.dialogVisible = false
              this.loadData()
            })
          }
        }
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
    getFeeTypeClass(type) {
      const classes = {
        'INTEREST': 'info',
        'FEE': 'success',
        'PENALTY': 'warning',
        'COMMITMENT': 'neutral',
        'OTHER': 'neutral'
      }
      return classes[type] || 'neutral'
    }
  }
}
</script>

<style scoped>
.tax-classifications {
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

.ellipsis {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
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

.action-btn.edit:hover {
  color: var(--fin-primary-700);
}

.action-btn.toggle:hover {
  color: #059669;
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