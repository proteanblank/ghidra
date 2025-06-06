/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.plugin.core.decompiler.taint.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import docking.action.KeyBindingData;
import docking.action.ToolBarData;
import generic.theme.GIcon;
import ghidra.app.plugin.core.decompiler.taint.TaintPlugin;
import ghidra.app.plugin.core.decompiler.taint.TaintState.QueryType;

public class TaintQueryDefaultAction extends TaintAbstractQueryAction {

	public TaintQueryDefaultAction(TaintPlugin plugin) {
		super(plugin, "DefaultQuery", "Default Taint Query", "Run default taint query");
		executeTaintQueryIconString = "icon.version.tracking.markup.status.conflict";
		executeTaintQueryIcon = new GIcon(executeTaintQueryIconString);
		queryType = QueryType.DEFAULT;

		setToolBarData(new ToolBarData(executeTaintQueryIcon));
		setKeyBindingData(new KeyBindingData(KeyEvent.VK_Y, InputEvent.SHIFT_DOWN_MASK));
	}

}
