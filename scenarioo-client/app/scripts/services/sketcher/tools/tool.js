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
/* eslint no-console:0 */


angular.module('scenarioo.services').factory('Tool', function (DrawingPadService, $log) {

    return function () {

        return {

            name: 'Tool name required',
            icon: 'default',
            tooltip: 'Tooltip text required',
            cursor: 'default',
            buttonDisabled: false,

            DRAWING_ENDED_EVENT: 'drawingEnded',


            getDrawingPad: function () {
                if (DrawingPadService.getDrawingPad().drawingContainer) {
                    return DrawingPadService.getDrawingPad().drawingContainer;
                }
            },

            activate: function () {
                $log.log('Activated tool: ' + this.name);

                this.buttonDisabled = true;
                var dp = this.getDrawingPad();
                if (dp) {
                    dp.on('mousedown.drawingpad', this.onmousedown);
                    dp.on('mouseup.drawingpad', this.onmouseup);
                    dp.on('mousemove.drawingpad', this.onmousedrag);
                }

                $('body').css('cursor', this.getCursor());
            },

            deactivate: function () {
                this.buttonDisabled = false;

                var dp = this.getDrawingPad();
                if (dp) {
                    dp.off('mousedown.drawingpad', this.onmousedown);
                    dp.off('mouseup.drawingpad', this.onmouseup);
                    dp.off('mousemove.drawingpad', this.onmousedrag);
                }

                $('body').css('cursor', this.cursor);
            },

            onmousedown: function () {
            },
            onmouseup: function () {
            },
            onmousedrag: function () {
            },

            getCursor: function () {
                return this.cursor;
            }
        };
    };
});
