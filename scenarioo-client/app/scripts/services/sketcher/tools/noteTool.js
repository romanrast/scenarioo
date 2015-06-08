/*scenarioo-client
 Copyright (C) 2015, scenarioo.org Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

angular.module('scenarioo.controllers').factory('NoteTool', function (AbstractShapeTool) {
    var tool = AbstractShapeTool.get;

    tool.name = 'Note Tool';
    tool.icon = null;
    tool.tooltip = 'This tool is used to add a note to the sketch.';
    var rect = null;


    tool.onmousedown = function (event) {
        tool.onmousedownTemplate(event);

        tool.component = tool.drawingPad.group();
        rect = tool.drawingPad.rect(0, 0, 0, 0);
        tool.component.add(rect);

        tool.component.attr({
            transform: 'translate(' + tool.originalX + ',' + tool.originalY + ')'
        });
        rect.attr({
            fill: '#eee'
        });
    };

    tool.onmouseup = function (event) {
        tool.onmouseupTemplate(event);
    };

    tool.onmousedrag = function (event) {
        if (!tool.mousedown) {
            return;
        }
        tool.onmousedragTemplate(event);

        tool.component.attr({
            //width: tool.cornerX - tool.anchorX,
            //height: tool.cornerY - tool.anchorY,
            transform: 'translate(' + tool.anchorX + ',' + tool.anchorY + ')'
        });

        rect.attr({
            width: tool.cornerX - tool.anchorX,
            height: tool.cornerY - tool.anchorY
        });
    };

    return {

        name: tool.name,
        icon: tool.icon,
        tooltip: tool.tooltip,
        cursor: tool.cursor,

        onmouseup: tool.onmouseup,
        onmousedown: tool.onmousedown,
        onmousedrag: tool.onmousedrag

    };

});