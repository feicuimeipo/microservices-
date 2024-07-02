import Vue from "vue";
import form from "@/api/form.js";

const state = {
    // 自定义对话框数据
    customDialogData: {},
    // 关联查询数据
    customQueryData: {}
}
const getters = {
    hasCustomQueryDataWithAlias: (state) => (alias) => {
        return state.customQueryData.hasOwnProperty(alias);
    }
}

const actions = {
    getCustomQuery({ commit, state, getters }, alias) {
        if (getters.hasCustomQueryDataWithAlias(alias)) {
            return;
        }
        form.getByAliasCq(alias, res => {
            commit("addCustomQueryData", res);
        });
    }
}

const mutations = {
    addCustomQueryData(state, data) {
        if (data && data.alias) {
            Vue.set(state.customQueryData, data.alias, data);
        }
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
